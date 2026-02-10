package org.zoxweb.client.widget.net;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RadioButton;

public class SystemWidget extends Composite {

    private static SystemWidgetUiBinder uiBinder = GWT.create(SystemWidgetUiBinder.class);
    public @UiField RadioButton rbReboot;
    public @UiField RadioButton rbShutdown;
    public @UiField Button bSend;

    interface SystemWidgetUiBinder extends UiBinder<Widget, SystemWidget> {
    }

    public SystemWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
