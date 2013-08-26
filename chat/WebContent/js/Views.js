/*
 * Copyright 2012-2013 Dennis Hörsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Provides access to different views. Views can be switched by calling show. they are inserted into the given viewId DOM element.
 *
 * Partials representing the view may contain placeholders (like '{{name}}') which will be replaced if a appropriate binding is given.
 */
function Views(viewId_) {
    'use strict';
    var toJQueryId = function(partialId) {
            return partialId.replace(/:/g, '\\:').replace(/\./g, '\\.');
        },

        viewId = "#" + toJQueryId(viewId_),
        pages = {},

        getContent = function(partialId) {
            var partial = jQuery("#" + toJQueryId(partialId));
            if (partial.length < 1) {
                throw "Partial id='" + partialId + "' not found!";
            }
            return partial.contents().text();
        },

        replaceBindings = function(html, bindings) {
            var result = html;
            for (var binding in bindings) {
                if (bindings.hasOwnProperty(binding)) {
                    var val = bindings[binding];
                    if(val.toViewString) {
                        val = val.toViewString();
                    }
                    result = result.replace("{{" + binding + "}}", val);
                }
            }
            return result;
        };

    return {
        registerPage: function(partialId, onReadyHandler) {
            pages[partialId] = {
                    'html': getContent(partialId),
                    'onReadyHandler': onReadyHandler
            };
        },

        show: function(partialId, bindings) {
            var page = pages[partialId];
            if (!page) {
                throw "Page '" + partialId + "' is undefined!";
            }
            var html = replaceBindings(page['html'], bindings);
            jQuery(viewId).fadeOut(function() {
                jQuery(viewId)
                    .html(html)
                    .fadeIn();

                page['onReadyHandler']();
            });

        },

        getPartial: function(partialId, bindings) {
            var partial = getContent(partialId);
            return replaceBindings(partial, bindings);
        }
    };
};