package org.eclipse.scout.boot.tabularius;

import java.util.List;

import org.eclipse.scout.boot.tabularius.numbers.NumbersPage;
import org.eclipse.scout.boot.tabularius.scans.DocumentPage;
import org.eclipse.scout.boot.ui.fonts.FontAwesomeIcons;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;

@Bean
public class TabulariusOutline extends AbstractOutline {

	@Override
	protected String getConfiguredTitle() {
		return "Tabularius";
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.fa_fileTextO;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		pageList.add(BEANS.get(DocumentPage.class));
		pageList.add(BEANS.get(NumbersPage.class));
	}
}
