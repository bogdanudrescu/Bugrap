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
package com.vaadin.tests.components.textarea;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * Ticket #14080
 *
 * - The bug happen on push event.<br/>
 * - The changes in the DOM are css related.<br/>
 * - It seems like when the class attribute is set on push, the textarea revert
 * to the height defined by the rows attribute.
 *
 * @since 2014.06.30
 * @author Vaadin Ltd
 */
@Theme("tests-tickets-14080")
// @Push(PushMode.AUTOMATIC)
public class TextAreaSizeResetted extends AbstractTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        setPollInterval(15000);

        final TextField textField = new TextField("height");

        final TextArea textArea = new TextArea();
        textArea.setSizeUndefined();
        textArea.setValue("This is a text.");

        Button button = new Button("Change Height", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                System.err.println("textField.getValue(): "
                        + textField.getValue());
                System.err.println("textArea.getState().height:before: "
                        + textArea.getHeight());

                textArea.setHeight(textField.getValue());
                // textArea.setSizeFull();

                System.err.println("textArea.getState().height:after: "
                        + textArea.getHeight());
            }
        });

        HorizontalLayout layout = new HorizontalLayout();

        addComponent(layout);

        layout.addComponent(textArea);
        layout.addComponent(textField);
        layout.addComponent(button);

        // v-textarea v-widget v-has-width
        // v-textarea v-widget v-has-width

    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14080;
    }

}
