package org.eclipse.scout.boot.tabularius.numbers;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Bean;

@Bean
public class NumbersPage extends AbstractPageWithNodes {
	
	@Override
	protected void execInitPage() {
		super.execInitPage();
		setLeaf(true);
	}

	@Override
	protected Class<? extends IForm> getConfiguredDetailForm() {
		return NumbersForm.class;
	}

	@Override
	protected String getConfiguredTitle() {
		return "\"Numbers\" Dataset Browser";
	}
}
