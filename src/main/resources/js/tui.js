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
    instrumentTables();
    instrumentMonitorFields();
    instrumentRefreshButtons();
    instrumentSearchForms();

    updateMonitorFields();
}

/*
    Calls the backend web service defined as 'source' in the component's attributes.
    Optional 'data' is a Map which contains parameters as (key, value) strings that will be sent as content (HTTP POST).
*/
async function refreshComponent(id, data) {
    const element = document.getElementById(id);
    setFetchData(element, data);
    const component = document.getElementById(id);
    const sourcePath = component.getAttribute('tui-source');
    console.log('refreshing component ' + id + ' with source: ' + sourcePath);
    const jsonBody = data === undefined ? ''
        : (data instanceof Map) ?
            JSON.stringify(Array.from(data.entries()))
            : JSON.stringify(Array.from(Object.entries(data)));

    fetch(sourcePath, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: jsonBody,
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
    return (typeof element.fetch_data === 'undefined') ? {} : element.fetch_data;
}

function createComponent(json) {
    const type = json['type'];
    var result;
    if(type == 'paragraph') {
        result = document.createElement('p');
        updateParagraph(result, json);
    } else if(type == 'grid') {
        result = document.createElement('div');
        result.classList.add('tui-grid');
        updateGrid(result, json);
    } else {
        result = null;
    }

    return result;
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

function updateGrid(gridElement, json) {
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
                childElement = createComponent(json[childName]);
            } else {
                childElement = document.createElement('p');
            }
            gridElement.appendChild(childElement);
        }
    }
}

// TABS

function selectTab(tabId, tabLink) {
    const tabPanels = document.querySelectorAll('.tui-panel');
    tabPanels.forEach(function(panel, i) {
        panel.style.display = 'none';
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
        button.addEventListener('click', function() {
              button.getAttribute('tui-refresh-listeners').split(",")
                  .forEach(function(id, i) {
                      refreshComponent(id);
                  });
        });
    });
}

// SEARCH

function instrumentSearchForms() {
    const searchForms = document.querySelectorAll('.tui-search-form');
    searchForms.forEach(function(searchElement, i) {
        const button = searchElement.querySelector('button');
        button.addEventListener('click', function() {
            const searched = searchElement.querySelectorAll("input[name='search']")[0].value;
            searchElement.getAttribute('tui-refresh-listeners').split(",")
                .forEach(function(id, i) {
                    refreshComponent(id, {search: searched});
                });
        });
    });
}

// FORMS

function instrumentForms() {
    const forms = document.querySelectorAll('.tui-form');
    forms.forEach(function(form, i) {
        instrumentFormWithErrorMessage(form);
        form.addEventListener('submit', e => {
            e.preventDefault();

            const url = form.action;
            const data = new FormData(e.target);

            fetch(url, {
                    method: form.method,
                    enctype: 'multipart/form-data',
                    body: new URLSearchParams(data)
                })
                .then(response => {
                     if(!response.ok) {
                        throw new Error(`HTTP error, status = ${response.status}`);
                    }
                    hideFetchErrorInElement(form);
                    form.classList.remove('fetch-error');
                    if(form.hasAttribute('refresh-listeners')) {
                        form.getAttribute('refresh-listeners').split(",")
                            .forEach(function(id, i) {
                                refreshComponent(id);
                            })
                    }
                })
                .catch(error => {
                    form.classList.add("fetch-error");
                    console.log(error);
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
        const cancelButton = form.querySelector('.tui-modal-form-cancel-button');
        const submitButton = form.querySelector('.tui-modal-form-submit-button');

        openButton.addEventListener('click', () => {
            dialog.showModal();
        });
        cancelButton.addEventListener('click', () => {
            dialog.close();
        });
        form.addEventListener('submit', e => {
            e.preventDefault();
            const url = form.action;
            const data = new FormData(e.target);

            fetch(url, {
                    method: form.method,
                    enctype: 'multipart/form-data',
                    body: new URLSearchParams(data)
                })
                .then(response => {
                    if(!response.ok) {
                        throw new Error(`HTTP error, status = ${response.status}`);
                    }
                    hideFetchErrorInElement(form);
                    form.classList.remove("fetch-error");
                    return response.json();
                })
                .then((json) => {
                    if(json['status'] == 'ok') {
                        const fields = form.querySelectorAll('input');
                        fields.forEach(function (field) {
                            field.removeAttribute('title');
                            field.classList.remove("tui-form-input-invalid");
                        });
                        if(formContainer.hasAttribute('refresh-listeners')) {
                            formContainer.getAttribute('refresh-listeners').split(",")
                                .forEach(function(id, i) {
                                    refreshComponent(id);
                                })
                        }
                        dialog.close();
                    } else {
                        Object.keys(json['errors']).forEach(function(key) {
                            const field = form.querySelector("[name='" + key + "']");
                            const message = json['errors'][key];
                            field.setAttribute('title', message);
                            field.classList.add("tui-form-input-invalid");
                        });

                        throw new Error(json['message']);
                    }
                })
                .catch(error => {
                    form.classList.add("fetch-error");
                    showFetchErrorInElement(form, error);
                });
        });

    });
}

function instrumentFormWithErrorMessage(formElement) {
     const errorMessageElement = document.createElement('div');
     errorMessageElement.setAttribute('class', 'fetch-error-message');
     formElement.insertBefore(errorMessageElement,formElement.firstChild);
}

function showFetchErrorInElement(element, error) {
    const errorDiv = element.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = error;
    errorDiv.style.display = 'block';
}

function hideFetchErrorInElement(element) {
    const errorDiv = element.querySelectorAll('.fetch-error-message')[0];
    errorDiv.innerText = '';
    errorDiv.style.display = 'none';
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
    if(tablePickerElement.hasAttribute('tui-refresh-listeners')) {
        const columns = Array.from(tablePickerElement.querySelectorAll("th")).map(cell => cell.textContent);

        for(const row of tablePickerElement.querySelectorAll("tbody tr")) {
            const values = Array.from(row.querySelectorAll("td")).map(cell => cell.textContent);

            const data = new Map();
            var colIndex = 0;
            for(const column of columns) {
                data.set(column, values[colIndex++]);
            }

            row.addEventListener("click", function () {
                tablePickerElement.getAttribute('tui-refresh-listeners').split(",")
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
    copySVGAttributes(json, newElement);  // Setting attributes given by backend
    const source = json['tui-source']; // Overriding with original attributes that are consistent with page structure
    if(source != '') {
        newElement.setAttribute('tui-source', source);
    }
    newElement.setAttribute('id', svgElement.getAttribute('id'));

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
        if(svgJson.hasOwnProperty(key) && key != 'type' && key != 'components') {
            svgElement.setAttribute(key, svgJson[key]);
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
                        const fieldDiv = fieldset.querySelector("[id='" + field.tuid + "']");
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