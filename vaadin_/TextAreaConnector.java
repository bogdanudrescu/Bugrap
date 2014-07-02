/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui.textarea;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Element;
import com.vaadin.client.Util.Size;
import com.vaadin.client.ui.VTextArea;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.ui.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

    @Override
    public TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        getWidget().addMouseUpHandler(new ResizeMouseUpHandler());
    }

    /*
     * Workaround to handle the resize on the mouse up.
     */
    private class ResizeMouseUpHandler implements MouseUpHandler {

        @Override
        public void onMouseUp(MouseUpEvent event) {
            Element element = getWidget().getElement();

            System.err.println("element.getStyle().getHeight(): "
                    + element.getStyle().getHeight() + " is null: "
                    + (element.getStyle().getHeight() == null));
            System.err.println("element.getStyle().getWidth(): "
                    + element.getStyle().getWidth() + " is null: "
                    + (element.getStyle().getWidth() == null));

            updateSize(element.getStyle().getHeight(), getState().height,
                    "height");
            updateSize(element.getStyle().getWidth(), getState().width, "width");

            // updateSize(element.getOffsetHeight(), getState().height,
            // "height");
            // updateSize(element.getOffsetWidth(), getState().width, "width");
        }

        /*
         * Update the specified size on the server.
         */
        private void updateSize(String sizeText, String stateSizeText,
                String sizeType) {

            System.err.println("mouseup state " + sizeType + ": "
                    + stateSizeText);

            Size stateSize = Size.fromString(stateSizeText);
            Size newSize = Size.fromString(sizeText);

            if (stateSize == newSize) { // both are null
                return;

                // } else if (stateSize == null) {
            } else if (newSize == null) {
                sizeText = "";

            } else if (stateSize != null // && newSize != null
                    && stateSize.equals(newSize)) {
                return;
            }

            System.err.println("mouseup in " + sizeType + ": " + sizeText);

            getConnection().updateVariable(getConnectorId(), sizeType,
                    sizeText, false);
        }

        /*
         * Update the specified size on the server.
         */
        // private void updateSize(int newSizePx, String stateSize, String
        // sizeType) {
        //
        // System.err.println("mouseup state " + sizeType + ": " + stateSize);
        //
        // if (stateSize == null
        // || !Size.fromValueUnit(newSizePx, Unit.PX).equals(
        // Size.fromString(stateSize))) {
        //
        // String sizeText = newSizePx + "px";
        // System.err.println("mouseup in " + sizeType + ": " + sizeText);
        //
        // // getState().height = heightText;
        // getConnection().updateVariable(getConnectorId(), sizeType,
        // sizeText, false);
        // }
        // }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    // @Override
    // public void onStateChanged(StateChangeEvent stateChangeEvent) {
    // System.err.println("onStateChanged:getState().height:"
    // + getState().height);
    //
    // String correctHeight = "";
    //
    // getState().height = correctHeight;
    //
    // super.onStateChanged(stateChangeEvent);
    //
    // getConnection().updateVariable(getConnectorId(), "height",
    // correctHeight, false);
    //
    // }

}
