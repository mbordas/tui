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

function onload() {
    instrumentForms();
    instrumentTables();

    updateDisplayMonitorFields();
}

// FORMS

function instrumentForms() {
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form, i) {
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
                    if(form.hasAttribute('refresh-listeners')) {
                        form.getAttribute('refresh-listeners').split(",")
                            .forEach(function(id, i) {
                                refreshComponent(id);
                            })
                    }
                })
        })
    });
}

function refreshComponent(id) {
    const element = document.getElementById(id);
    if(element.nodeName == "TABLE") {
        refreshTable(id);
    }
}


// TABLES

function instrumentTables() {
    const tables = document.querySelectorAll('table');
    tables.forEach(function(table, i) {
        if(table.hasAttribute('tui-source')) {
            const sourcePath = table.getAttribute('tui-source');

            const tableContainer = document.createElement("div");
            tableContainer.classList.add("tui-container-table");

            const buttonRefresh = document.createElement("button");
            buttonRefresh.setAttribute('type', 'button');
            buttonRefresh.textContent = 'Refresh';
            buttonRefresh.addEventListener('click', function(){
                refreshTable(table.id);
            });
            tableContainer.append(buttonRefresh);

            table.replaceWith(tableContainer);
            tableContainer.append(table);
        }
    });
}

async function refreshTable(id) {
    const table = document.getElementById(id);
    const sourcePath = table.getAttribute('tui-source');
    console.log("refreshing table " + id + " with source: " + sourcePath);
    const response = await fetch(sourcePath);
    const json = await response.json();
    console.log(json);

    const freshBody = document.createElement("tbody");
    for(var r = 0; r < json['tbody'].length; r++) {
        const row = json['tbody'][r];
        const freshRow = document.createElement("tr");
        for(var c = 0; c < row.length; c++) {
            const cell = row[c];
            const freshCell = document.createElement("td");
            freshRow.append(freshCell);
            freshCell.textContent = cell;
        }
        freshBody.append(freshRow);
    }

    table.getElementsByTagName("tbody")[0].replaceWith(freshBody);
}

// MONITORING

function updateDisplayMonitorFields() {
    const fields = document.querySelectorAll('.tui-monitor-field');
    fields.forEach(function(field, i) {
        const value = field.getAttribute('value');
        const valueSpans = field.querySelectorAll('.tui-monitor-field-value');
        valueSpans.forEach(function(valueSpan, i) {
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
    });
}