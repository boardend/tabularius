package org.eclipse.scout.boot.tabularius.anagnostes;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Bean;

@Bean
public class AnagnostesPage extends AbstractPageWithNodes {

	@Override
	protected void execInitPage() {
		super.execInitPage();
		setLeaf(true);
	}

	@Override
	protected Class<? extends IForm> getConfiguredDetailForm() {
		return AnagnostesForm.class;
	}

	@Override
	protected String getConfiguredTitle() {
		return "Anagnostes";
	}
}
