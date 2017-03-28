package org.eclipse.scout.boot.tabularius.scans;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Bean;

@Bean
public class DocumentPage extends AbstractPageWithNodes {

	@Override
	protected void execInitPage() {
		super.execInitPage();
		setLeaf(true);
	}

	@Override
	protected Class<? extends IForm> getConfiguredDetailForm() {
		return DocumentForm.class;
	}

	@Override
	protected String getConfiguredTitle() {
		return "Document";
	}

	@Override
	protected void execInitDetailForm() {
		if(getDetailForm() instanceof DocumentForm) {
			((DocumentForm) getDetailForm()).execInitForm();
		}
	}
}
