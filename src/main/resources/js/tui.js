/* Copyright (c) 2024, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
    Naming the functions
    - instrumentXXX: completes the DOM structure, adds triggers and code.
    - updateXXX: updates the content and the display.
    - refreshXXX: calls the backend in order to get fresh data, then updates and instruments when needed.
*/

"use strict";

function onload() {
    instrumentForms();
    instrumentModalForms();
    instrumentNavButtons();
    instrumentTables();
    instrumentMonitorFields();
    instrumentRefreshButtons();
    instrumentSearchForms();

    updateMonitorFields();
}

/*
    Calls the backend web service defined as 'source' in the component's attributes.
    Optional 'data' is a regular Object which contains attributes as (key, value) strings that will be sent as content (HTTP POST).
*/
async function refreshComponent(id, data) {
    const element = document.getElementById(id);
    setFetchData(element, data);
    const component = document.getElementById(id);
    const sourcePath = component.getAttribute('tui-source');
    console.log('refreshing component ' + id + ' with source: ' + sourcePath);

     if(data === undefined) {
         data = new Map();
         Object.entries(SESSION_PARAMS).forEach(([key, value]) => {
            data.set(key, value);
            console.log(`${key}: ${value}`);
         });
     } else {
         for(let key in SESSION_PARAMS) {
            data[key] = SESSION_PARAMS[key];
         }
    }

    var body;
    var headers;

    if(FETCH_TYPE == 'JSON') {
        if(data instanceof Map) {
            body = JSON.stringify(Array.from(data.entries()));
        } else {
            body = JSON.stringify(Array.from(Object.entries(data)));
        }
        headers = { 'Content-Type': 'application/json' };
    } else if(FETCH_TYPE == 'FORM_DATA') {
        body = new FormData();
        for(let key in data) {
            body.append(key, data[key]);
        }
        headers = {};
    }

    fetch(sourcePath, {
            method: 'POST',
            headers: headers,
            body: body,
        })
        .then(response => {
            if(!response.ok) {
                throw new Error(`HTTP error, status = ${response.status}`);
            }
            hideFetchError(component);
            return response.json();
        })
        .then((json) => {
            const type = json['type'];
            if(type == 'paragraph') {
                updateParagraph(component, json);
            } else if(type == 'table' || type == 'tablepicker' || type == 'table-data') {
                updateTable(component, json);
            } else if(type == 'svg') {
                updateSVG(component, json);
            } else if(type == 'grid') {
                updateGrid(component, json);
            } else if(type == 'panel') {
                updatePanel(component, json);
            } else {
                console.error('element with id=' + id + ' could not be refreshed. Type of received json is not supported: ' + type);
            }
        })
        .catch(error => {
            showFetchError(component, error);
        });
}

/*
    Links parameters map 'data' to the element so that it will be used to complete the future Ajax requests when refreshing the element.
*/
function setFetchData(element, data) {
    element.fetch_data = data;
}

/*
    Gives the parameters map that are linked to the element. These parameters must be added to any refreshing Ajax request.
*/
function getFetchData(element) {
    const result = (typeof element.fetch_data === 'undefined') ? {} : element.fetch_data;
    for(let key in SESSION_PARAMS) {
        data[key] = SESSION_PARAMS[key];
    }
    return result;
}

function updatePanel(panelElement, json, idMap) {
    panelElement.innerHTML = '';
    setTextAlignClass(panelElement, json['textAlign']);
    if(idMap == null) {
        idMap = new Map();
    }
    idMap.set(json['tuid'], panelElement.id);
    for(var child of json['content']) {
        const element = createComponent(child, idMap);
        if(element == null) {
            console.error('Unable to create component from type: ' + child['type']);
        } else {
            panelElement.appendChild(element);
        }
    }
}

function setTextAlignClass(element, textAlign) {
    element.classList.remove('tui-align-left');
    element.classList.remove('tui-align-center');
    element.classList.remove('tui-align-right');
    element.classList.remove('tui-align-stretch');
    element.classList.add('tui-align-' + textAlign.toLowerCase());
}

/*
    idMap (optional) gives some TUID given by backend to be replaced in order to match the frontend current ids.
    This map should be passed to any element that contains other elements.
*/
function createComponent(json, idMap) {
    const type = json['type'];
    var result;
    if(type == 'panel') {
        result = document.createElement('div');
        result.id = json['tuid'];
        updatePanel(result, json, idMap);
    } else if(type == 'paragraph') {
        result = document.createElement('p');
        updateParagraph(result, json);
    } else if(type == 'grid') {
        result = document.createElement('div');
        result.classList.add('tui-grid');
        updateGrid(result, json, idMap);
    } else if(type == 'verticalFlow') {
        result = document.createElement('div');
        result.classList.add('tui-vertical-flow');
        result.classList.add('tui-grid');
        updateVerticalFlow(result, json, idMap);
    } else if(type == 'tabbedFlow') {
        result = document.createElement('div');
        result.classList.add('tui-tabbedflow');
        updateTabbedFlow(result, json, idMap);
    } else if(type == 'verticalScroll') {
        result = document.createElement('div');
        result.classList.add('tui-vertical-scroll');
        updateVerticalScroll(result, json, idMap);
    } else if(type == 'refreshButton') {
        result = document.createElement('button');
        result.classList.add('tui-refresh-button');
        result.setAttribute('type', 'button');
        result.setAttribute('tui-key', json['key']);
        result.setAttribute('tui-refresh-listeners', adaptRefreshListeners(json['refreshListeners'], idMap));
        result.textContent = json['label'];
        instrumentRefreshButton(result);
    } else if (type == 'navbutton') {
        result = document.createElement('form');
        result.classList.add('tui-navbutton');
        result.setAttribute('method', 'POST');
        result.setAttribute('action', json['target']);
        Object.entries(json['parameters']).forEach(([key, value]) => {
            const input = document.createElement('input');
            input.setAttribute('type', 'hidden');
            input.setAttribute('name', key);
            input.setAttribute('value', value);
            result.appendChild(input);
        });
        const button = document.createElement('button');
        button.setAttribute('type', 'submit');
        button.textContent = json['label'];
        result.appendChild(button);
        instrumentNavButton(result);
    } else if(type == 'navlink') {
        result = document.createElement('a');
        result.classList.add('tui-navlink');
        result.setAttribute('href', json['target']);
        result.textContent = json['label'];
    } else if(type == 'svg') {
        const containedElement = createElementWithContainer('svg', 'tui-container-svg');
        updateSVG(containedElement.element, json);
        result = containedElement.container;
    } else if(type == 'image') {
        result = document.createElement('img');
        result.setAttribute('src', json['source']);
        result.setAttribute('alt', json['text']);
    } else {
        result = null;
    }

    if(result != null && json['style'] != null) {
        var style = '';
        Object.entries(json['style']).forEach(([key, value]) => {
            style += key + ':' + value + ';';
        });
        result.setAttribute('style', style);
    }

    return result;
}

/*
    Replaces 'idsSeparatedByComa' where ids are found in 'idMap'.
*/
function adaptRefreshListeners(idsSeparatedByComa, idMap) {
    if(idMap == null) {
        return idsSeparatedByComa;
    } else {
        var result = '';
        idsSeparatedByComa.split(",").forEach(function(id, i) {
            if(idMap.has(id)) {
                result = result + idMap.get(id) + ',';
            } else {
                result = result + id + ',';
            }
        });
        if(result.endsWith(',')) {
            return result.slice(0, -1);
        } else {
            return result;
        }
    }
}

function createElementWithContainer(name, containerClass) {
    const containerElement = document.createElement('div');
    containerElement.classList.add(containerClass);
    const newElement = document.createElement(name);
    containerElement.append(newElement);

    instrumentWithErrorMessage(containerElement);

    return {container: containerElement, element: newElement};
}

function instrumentWithErrorMessage(element) {
     const errorMessageElement = document.createElement('div');
     errorMessageElement.setAttribute('class', 'fetch-error-message');
     element.insertBefore(errorMessageElement,element.firstChild);
}

function showFetchError(element, error) {
    const containerElement = element.parentElement;
    containerElement.classList.add('fetch-error');
    const errorDiv = containerElement.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = error.message;
    errorDiv.style.display = 'block';
}

function hideFetchError(element) {
    const containerElement = element.parentElement;
    containerElement.classList.remove('fetch-error');
    const errorDiv = containerElement.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = '';
    errorDiv.style.display = 'none';
}

// GRIDS

function updateGrid(gridElement, json, idMap) {
    const rows = parseInt(json['rows']);
    const columns = parseInt(json['columns']);
    gridElement.style.gridTemplateRows = 'auto '.repeat(rows);
    gridElement.style.gridTemplateColumns = 'auto '.repeat(columns);
    gridElement.innerHTML = '';

    for(var row = 0; row < rows; row++) {
        for(var column = 0; column < columns; column++) {
            const childName = '' + row + '_' + column;
            var childElement;
            if(Object.hasOwn(json, childName)) {
                childElement = createComponent(json[childName], idMap);
            } else {
                childElement = document.createElement('p');
            }
            gridElement.appendChild(childElement);
        }
    }
}

// FLOWS

function updateVerticalFlow(flowElement, json, idMap) {
    flowElement.style.placeItems = 'center';
    flowElement.style.gridTemplateRows = 'auto';
    switch(json['width']) {
        case 'MAX': flowElement.style.gridTemplateColumns = '0% 100% 0%'; break;
        case 'WIDE': flowElement.style.gridTemplateColumns = 'min-content 1fr min-content'; break;
        case 'NORMAL': flowElement.style.gridTemplateColumns = '1fr min-content 1fr'; break;
    }
    const preParagraph = document.createElement('p');
    giveMarginReadingProperties(preParagraph, json['width']);
    flowElement.appendChild(preParagraph);
    const flowContainer = document.createElement('div');
    flowElement.appendChild(flowContainer);
    flowContainer.style.display = 'grid';
    flowContainer.style.gridTemplateRows = 'auto';
    for(var child of json['content']) {
        const childContainer = document.createElement('div');
        flowContainer.appendChild(childContainer);
        childContainer.style.textAlign = 'center';
        const childElement = createComponent(child, idMap);
        childContainer.appendChild(childElement);
    }
    const postParagraph = document.createElement('p');
    giveMarginReadingProperties(postParagraph, json['width']);
    flowElement.appendChild(postParagraph);
}

function giveCenterReadingProperties(centerContainer, width) {
    if(width == 'NORMAL') {
        pElement.classList.add('tui-reading-normal-area');
    }
}

function giveMarginReadingProperties(pElement, width) {
    if(width == 'WIDE') {
        pElement.classList.add('tui-reading-wide-margin');
    }
}

function updateVerticalScroll(scrollElement, json, idMap) {
    scrollElement.style.height = '' + json['height_px'] + 'px';
    for(var child of json['content']) {
        const childElement = createComponent(child, idMap);
        scrollElement.appendChild(childElement);
    }
}

// TABS

function updateTabbedFlow(tabbedFlowElement, json, idMap) {
    const tabsNav = document.createElement('div');
    tabbedFlowElement.appendChild(tabsNav);
    tabsNav.classList.add('tui-tabnav');

    var index = 1;
    for(var tab of json['tabs']) {
        const flowTUID = tab['content']['tuid'];
        const flow = createComponent(tab['content'], idMap);
        flow.setAttribute('id', flowTUID);
        flow.style.width = '100%';
        flow.classList.add('tui-tab');
        tabbedFlowElement.appendChild(flow);

        const button = document.createElement('button');
        button.classList.add('tui-tablink');
        tabsNav.appendChild(button);
        if(index == 1) {
            button.classList.add('tui-tablink-active');
            flow.style.display = 'block';
        } else {
            flow.style.display = 'none';
        }
        button.textContent = tab['title'];
        button.onclick = function() {
            selectTab(flowTUID, this);
        };

        index++;
    }
}

function selectTab(tabId, tabLink) {
    const tabs = tabLink
        .parentElement // tabnav
        .parentElement // tabbedFlow element, pdirect children are: tabsnav and tabs
        .querySelectorAll('.tui-tab');
    tabs.forEach(function(tab, i) {
        tab.style.display = 'none';
    });

    document.getElementById(tabId).style.display = 'block';

    const tabLinks = document.querySelectorAll('.tui-tablink');
    tabLinks.forEach(function(link, i) {
        link.setAttribute('class', 'tui-tablink');
    });
    tabLink.setAttribute('class', 'tui-tablink tui-tablink-active');
}

// PARAGRAPHS

function updateParagraph(element, json) {
    element.innerHTML = '';
    element.className = '';
    element.classList.add('tui-align-' + json['textAlign'].toLowerCase());
    element.classList.add('tui-border-' + json['border']);
    for(var fragment of json['content']) {
        const fragmentType = fragment[0];
        if(fragmentType == 'text') {
            element.innerHTML += fragment[1];
        } else if(fragmentType == 'strong') {
            element.innerHTML += '<strong>' + fragment[1] + '</strong>';
        }
    }
}

// REFRESH BUTTONS

function instrumentRefreshButtons() {
    const refreshButtons = document.querySelectorAll('.tui-refresh-button');
    refreshButtons.forEach(function(button, i) {
       instrumentRefreshButton(button);
    });
}

function instrumentRefreshButton(buttonElement) {
     buttonElement.addEventListener('click', function() {
        const data = buttonElement.hasAttribute('tui-key') ? { key: buttonElement.getAttribute('tui-key')} : {};
        buttonElement.getAttribute('tui-refresh-listeners').split(",")
        .forEach(function(id, i) {
            refreshComponent(id, data);
        });
    });
}

// SEARCH

function instrumentSearchForms() {
    const searchForms = document.querySelectorAll('.tui-search-form');
    searchForms.forEach(function(searchElement, i) {
        const searchInput = searchElement.querySelectorAll("input[type='search']")[0];
        const button = searchElement.querySelector('button');
        button.addEventListener('click', function() {
            searchElement.getAttribute('tui-refresh-listeners').split(",")
                .forEach(function(id, i) {
                    const data = {};
                    data[searchInput.getAttribute('name')] = searchInput.value;
                    refreshComponent(id, data);
                });
        });
        searchInput.addEventListener('keypress', function(event) {
            if(event.key === 'Enter') {
                event.preventDefault();
                button.click();
            }
        });
    });
}

// FORMS

function instrumentForms() {
    const forms = document.querySelectorAll('.tui-form');
    forms.forEach(function(form, i) {
        instrumentWithErrorMessage(form);

        const resetButton = form.querySelector('.tui-form-reset-button');
        resetButton.addEventListener('click', () => {
            form.reset();
            completeFormReset(form);
        });

        form.addEventListener('submit', e => {
            e.preventDefault();

            const url = form.action;
            hideFetchErrorInElement(form);
            hideSuccessMessage(form);
            startFormPending(form);
            fetch(url, {
                    method: form.method,
                    enctype: 'multipart/form-data',
                    body: prepareFormData(form)
                })
                .then(response => {
                    if(!response.ok) {
                        throw new Error(`HTTP error, status = ${response.status}`);
                    }
                    hideFetchErrorInElement(form);
                    stopFormPending(form);
                    return response.json();
                })
                .then((json) => {
                    onFormResponse(form, json);
                })
                .catch(error => {
                    stopFormPending(form);
                    showFetchErrorInElement(form, error)
                });
        })
    });
}

function instrumentModalForms() {
    const modalFormsContainers = document.querySelectorAll('.tui-modal-form');
    modalFormsContainers.forEach(function(formContainer, i) {
        const openButton = formContainer.querySelector('button');
        const dialog = formContainer.querySelector('dialog');
        const form = dialog.querySelector('form');
        const closeButton = form.querySelector('.tui-form-close-button');
        const submitButton = form.querySelector('.tui-form-submit-button');

        openButton.addEventListener('click', () => {
            dialog.showModal();
        });
        closeButton.addEventListener('click', () => {
            hideSuccessMessage(form);
            dialog.close();
        });
        form.addEventListener('reset', e => {
            form.reset();
            completeFormReset(form);
        });
        form.addEventListener('submit', e => {
            e.preventDefault();
            const url = form.action;
            hideFetchErrorInElement(form);
            hideSuccessMessage(form);
            startFormPending(form);
            fetch(url, {
                    method: form.method,
                    enctype: 'multipart/form-data',
                    body: prepareFormData(form)
                })
                .then(response => {
                    if(!response.ok) {
                        throw new Error(`HTTP error, status = ${response.status}`);
                    }
                    hideFetchErrorInElement(form);
                    stopFormPending(form);
                    return response.json();
                })
                .then((json) => {
                    onFormResponse(form, json);
                })
                .catch(error => {
                    stopFormPending(form);
                    showFetchErrorInElement(form, error);
                });
        });

    });
}

/*
    Builds the body to be sent to backend with the following rules:
    - A file input is renamed with prefixed '_file_' so it can be easily detected by the backend.
    - Any other type of input is added without modification
    - Any session parameter is added, but may be overridden by input with same name
*/
function prepareFormData(formElement) {
    const data = new FormData();

    // Session parameters
    for(let key in SESSION_PARAMS) {
        data[key] = SESSION_PARAMS[key];
    }

    // Form inputs
    formElement.querySelectorAll('input').forEach(function(inputElement) {
        if(inputElement.type == 'file' && inputElement.files[0] != null) {
            data.append('_file_' + inputElement.name, inputElement.files[0], inputElement.files[0].name);
        } else if(inputElement.type == 'radio') {
            if(inputElement.checked) {
                data.append(inputElement.name, inputElement.value);
            }
        } else if(inputElement.type == 'checkbox') {
             if(inputElement.checked) {
                 data.append(inputElement.name, 'on');
             } else {
                 data.append(inputElement.name, 'off');
             }
         } else {
            if(inputElement.value != '') {
                data.append(inputElement.name, inputElement.value);
            }
        }
    });
    return data;
}



function startFormPending(formElement) {
    const fieldset = formElement.querySelector('fieldset');
    fieldset.classList.add('form-pending');
    fieldset.disabled = true;
}

function stopFormPending(formElement) {
    const fieldset = formElement.querySelector('fieldset');
    fieldset.classList.remove('form-pending');
    fieldset.disabled = false;
}

function completeFormReset(formElement) {
    hideSuccessMessage(formElement);
    hideFetchErrorInElement(formElement);
    hideInputsErrors(formElement);
}

function hideInputsErrors(formElement) {
    const fields = formElement.querySelectorAll('input');
    fields.forEach(function (field) {
        field.removeAttribute('title');
        field.classList.remove("tui-form-input-invalid");
        const errorElement = field.parentElement.querySelector('.tui-input-error');
        if(errorElement != null) { // input elements of type radio option do not have an error element.
            errorElement.textContent = '';
        }
    });
}

function onFormResponse(formElement, json) {
    hideFetchErrorInElement(formElement);
    stopFormPending(formElement);

    if(json['status'] == 'ok') {
        hideInputsErrors(formElement);

        // Show success message
        const messageElement = formElement.querySelector('#form-message-' + formElement.id);
        messageElement.classList.add('tui-monitor-field-value-green');
        messageElement.textContent = json['message'];

        if(formElement.hasAttribute('refresh-listeners')) {
            formElement.getAttribute('refresh-listeners').split(",")
                .forEach(function(id, i) {
                    refreshComponent(id);
                })
        }

        if(json['formUpdate'] != null) {
            const formUpdate = json['formUpdate'];
            if(formUpdate['type'] != 'form') {
                console.error('Unexpected type: ' + formUpdate['type']);
            } else {
                formElement.setAttribute('action', formUpdate['target']); // Updating target
                formElement.querySelector("button[type='submit']").textContent = formUpdate['submitLabel']; // Updating submit label

                const fieldset = formElement.querySelector('fieldset');
                const legend = fieldset.querySelector('legend'); // Updating title
                legend.textContent = formUpdate['title'];

                const formTUID = formElement['id'];
                const newInputsDiv = document.createElement('div'); // Inputs are contained in a div that is child of fieldset
                formUpdate['inputs'].forEach(function(input) {
                    createFormInput(newInputsDiv, input, formTUID);
                });
                fieldset.querySelector('div').replaceWith(newInputsDiv);
            }
        } else if(formElement.getAttribute('tui-opens-page') != null) {
            const openForm = document.createElement('form');
            openForm.setAttribute('method', 'POST');
            openForm.setAttribute('action', formElement.getAttribute('tui-opens-page'));
            openForm.setAttribute('target', '_self');
            if(json['parameters'] != null) {
                for(let key in json['parameters']) {
                    const parameterInput = document.createElement('input');
                    parameterInput.setAttribute('type', 'hidden');
                    parameterInput.setAttribute('name', key);
                    parameterInput.setAttribute('value', json['parameters'][key]);
                    openForm.appendChild(parameterInput);
                };
            }
            formElement.appendChild(openForm);
            openForm.submit();
        }
    } else {
         Object.keys(json['errors']).forEach(function(key) {
            const field = formElement.querySelector("[name='" + key + "']");
            field.classList.add("tui-form-input-invalid");
            const errorElement = field.parentElement.querySelector('.tui-input-error');
            errorElement.textContent = json['errors'][key];
        });
    }
}

function createFormInput(fieldsetElement, json, formTUID) {
    const inputDiv = document.createElement('div');
    fieldsetElement.append(inputDiv);
    inputDiv.classList.add('tui-form-input');
    const inputId = formTUID + '-' + json['name'];
    const inputLabel = document.createElement('label');
    inputDiv.append(inputLabel);
    inputLabel.setAttribute('for', inputId);
    inputLabel.textContent = json['label'];

    if(json['type'] != 'from_input_checkbox') {
        inputLabel.classList.add('label-checkbox');
        const inputElement = document.createElement('input');
        inputDiv.append(inputElement);
        inputElement.setAttribute('type', json['type']);
        inputElement.setAttribute('name', json['name']);
    }
}

function hideSuccessMessage(formElement) {
    const messageElement = formElement.querySelector('#form-message-' + formElement.id);
    messageElement.textContent = ' ';
}

function showFetchErrorInElement(formElement, error) {
    console.log(error);
    formElement.classList.add('fetch-error');
    const errorDiv = formElement.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = error;
    errorDiv.style.display = 'block';
}

function hideFetchErrorInElement(formElement) {
    formElement.classList.remove('fetch-error');
    const errorDiv = formElement.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = '';
    errorDiv.style.display = 'none';
}

// NAV BUTTONS

function instrumentNavButtons() {
    const navbuttons = document.querySelectorAll('.tui-navbutton');
    navbuttons.forEach(function(navbutton, i) {
        instrumentNavButton(navbutton);
    });
}

function instrumentNavButton(element) {
     for(let key in SESSION_PARAMS) {
        const parameterInput = document.createElement('input');
        parameterInput.setAttribute('type', 'hidden');
        parameterInput.setAttribute('name', key);
        parameterInput.setAttribute('value', SESSION_PARAMS[key]);
        element.appendChild(parameterInput);
    }
}

// TABLES

function instrumentTables() {
    const tables = document.querySelectorAll('table');
    tables.forEach(function(table, i) {
        if(table.hasAttribute('tui-source')) {
            const sourcePath = table.getAttribute('tui-source');

            if(table.hasAttribute('tui-page-size')) {
                const pageSize = parseInt(table.getAttribute('tui-page-size'));
                const tableNavigation = document.createElement('div');
                tableNavigation.classList.add('tui-table-navigation');

                const previousPageButton = document.createElement('button');
                previousPageButton.type = 'button';
                previousPageButton.innerHTML = '<';
                previousPageButton.onclick = function() {
                    const pageNumber = parseInt(table.getAttribute('tui-page-number'));
                    const fetchData = getFetchData(table);
                    fetchData.page_size = pageSize;
                    fetchData.page_number = Math.max(1, pageNumber - 1);
                    refreshComponent(table.id, fetchData);
                };
                tableNavigation.append(previousPageButton);

                const nextPageButton = document.createElement('button');
                nextPageButton.type = 'button';
                nextPageButton.innerHTML = '>';
                nextPageButton.onclick = function() {
                    const pageNumber = parseInt(table.getAttribute('tui-page-number'));
                    const lastPageNumber = parseInt(table.getAttribute('tui-last-page-number'));
                    const fetchData = getFetchData(table);
                    fetchData.page_size = pageSize;
                    fetchData.page_number = Math.min(pageNumber + 1, lastPageNumber);
                    refreshComponent(table.id, fetchData);
                };
                tableNavigation.append(nextPageButton);

                const pageLocationSpan = document.createElement('span');
                tableNavigation.append(pageLocationSpan);

                const tableContainer = table.parentElement;
                tableContainer.append(tableNavigation);

                const tableSize = parseInt(table.getAttribute('tui-table-size'));
                const firstItemNumber = parseInt(table.getAttribute('tui-first-item-number'));
                const lastItemNumber = parseInt(table.getAttribute('tui-last-item-number'));
                updateTableNavigation(table, tableSize, firstItemNumber, lastItemNumber);
            }
        }

        instrumentTablePicker(table);
    });
}

function instrumentTablePicker(tablePickerElement) {

    // Getting optional refresh listeners ids, either from table attributes or in table's container attributes.
    var refreshListenersAttribute = null;
    if(tablePickerElement.hasAttribute('tui-refresh-listeners')) { // table has the attribute
        refreshListenersAttribute = tablePickerElement.getAttribute('tui-refresh-listeners');
    } else {
        var optionalContainerElement = tablePickerElement.parentElement;
        if(optionalContainerElement) { // table is in a container that has the attribute
            refreshListenersAttribute = optionalContainerElement.getAttribute('tui-refresh-listeners');
        }
    }

    if(refreshListenersAttribute != null) {
    /* if(tablePickerElement.hasAttribute('tui-refresh-listeners')) {  */
        const columns = Array.from(tablePickerElement.querySelectorAll("th")).map(cell => cell.textContent);

        for(const row of tablePickerElement.querySelectorAll("tbody tr")) {
            const values = Array.from(row.querySelectorAll("td")).map(cell => cell.textContent);

            const data = {};
            var colIndex = 0;
            for(const column of columns) {
                data[column] = values[colIndex++];
            }

            row.addEventListener("click", function () {
                refreshListenersAttribute.split(",")
                    .forEach(function(id, i) {
                        refreshComponent(id, data);
                    })
            });
        }
    }
}

async function updateTable(tableElement, json) {
    const freshBody = document.createElement('tbody');
    const hiddenColumnsIndexes = 'hiddenColumns' in json? json['hiddenColumns'] : [];
    for(var r = 0; r < json['tbody'].length; r++) {
        const row = json['tbody'][r];
        const freshRow = document.createElement("tr");
        for(var c = 0; c < row.length; c++) {
            const cell = row[c];
            const freshCell = document.createElement("td");
            if(hiddenColumnsIndexes.includes(c)) {
                freshCell.classList.add("tui-hidden-column");
            }
            freshRow.append(freshCell);
            freshCell.textContent = cell;
        }
        freshBody.append(freshRow);
    }

    if('pageNumber' in json) {
        tableElement.setAttribute('tui-page-number', json['pageNumber']);
        tableElement.setAttribute('tui-last-page-number', json['lastPageNumber']);
        updateTableNavigation(tableElement, json['tableSize'], json['firstItemNumber'], json['lastItemNumber']);
    }

    tableElement.getElementsByTagName('tbody')[0].replaceWith(freshBody);

    instrumentTablePicker(tableElement.parentElement);
}

function updateTableNavigation(tableElement, tableSize, firstItemNumber, lastItemNumber) {
    tableElement
        .parentElement
        .getElementsByClassName('tui-table-navigation')[0]
        .getElementsByTagName('span')[0]
        .innerText = `${firstItemNumber} - ${lastItemNumber} (${tableSize})`;
}

// SVG

function updateSVG(svgElement, json) {
    const newElement = document.createElementNS("http://www.w3.org/2000/svg", "svg"); // Creating the SVG tag with same id
    newElement.setAttribute('id', svgElement.getAttribute('id'));
    newElement.setAttribute('tui-source', svgElement.getAttribute('tui-source')); // keeping source
    copySVGAttributes(json, newElement);  // Setting attributes given by backend

    const svgContainer = svgElement.parentElement;
    svgContainer.removeChild(svgElement);
    svgContainer.appendChild(newElement);

    // Creating SVG components
    const jsonComponents = Array.from(json['components']);
    jsonComponents.forEach(function(jsonComponent, i) {
        const svgElement = document.createElementNS("http://www.w3.org/2000/svg", jsonComponent['type']);
        copySVGAttributes(jsonComponent, svgElement);
        newElement.appendChild(svgElement);
    });
}

function copySVGAttributes(svgJson, svgElement) {
    for(let key in svgJson) {
        if(svgJson.hasOwnProperty(key)) {
            if(key == 'title') {
                const title = document.createElementNS("http://www.w3.org/2000/svg", 'title');
                title.textContent = svgJson[key];
                svgElement.appendChild(title);
            } else if(key == 'innerText') {
                svgElement.textContent = svgJson[key];
            } else if(key != 'type' && key != 'components') {
                svgElement.setAttribute(key, svgJson[key]);
            }
        }
    }
}


// MONITORING

function updateMonitorFields() {
    const fields = document.querySelectorAll('.tui-monitor-field');
    fields.forEach(function(field, i) {
        const value = field.getAttribute('value');
        const valueSpan = field.querySelectorAll('.tui-monitor-field-value')[0];
        switch(value) {
            case 'GREEN':
                valueSpan.setAttribute('class', 'tui-monitor-field-value tui-monitor-field-value-green');
                break;
            case 'RED':
                valueSpan.setAttribute('class', 'tui-monitor-field-value tui-monitor-field-value-red');
                break;
            case 'NEUTRAL':
                valueSpan.setAttribute('class', 'tui-monitor-field-value tui-monitor-field-value-neutral');
                break;
            default:
                error('Unsupported value: ' + value);
        }
    });
}

async function refreshMonitorFields(sourcePath, fieldset) {
    fetch(sourcePath)
        .then(response => response.json())
        .then(json => {
            hideFetchErrorInElement(fieldset);
            fieldset.classList.remove("fetch-error");
            for(const field of json.fields) {
                switch(field.type) {
                    case 'monitor-field-greenred':
                        // Selecting element by numeric id must be handled that (weird) way
                        const fieldDiv = document.querySelector("[monitor-field-name='" + field.name + "']");
                        if(fieldDiv) {
                            fieldDiv.setAttribute('value', field.value);
                            const valueSpan = fieldDiv.querySelector('.tui-monitor-field-value');
                            valueSpan.innerText = field.text;
                        }
                    break;
                }
            }
        })
        .catch(error => {
            fieldset.classList.add("fetch-error");
            console.log(error);
            showFetchErrorInElement(fieldset, error)
        });

    updateMonitorFields();
}

function instrumentMonitorFields() {
    const fieldsets = document.querySelectorAll('.tui-monitor-fieldset');
    fieldsets.forEach(function(fieldset, i) {
        const sourcePath = fieldset.getAttribute('tui-source');
        const period_s = parseInt(fieldset.getAttribute('auto-refresh-period_s'));
        function refresh() {
            refreshMonitorFields(sourcePath, fieldset);
        }
        const intervalId = setInterval(refresh, 1000 * period_s);
    });
}