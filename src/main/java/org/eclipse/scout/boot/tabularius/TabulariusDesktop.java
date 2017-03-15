package org.eclipse.scout.boot.tabularius;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.scout.boot.tabularius.numbers.model.Contributor;
import org.eclipse.scout.boot.tabularius.numbers.util.NumbersFileWalker;
import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons;
import org.eclipse.scout.boot.ui.scout.AbstractScoutBootDesktop;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktopExtension;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.config.PlatformConfigProperties.ApplicationNameProperty;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;

@Bean
public class TabulariusDesktop extends AbstractScoutBootDesktop {
	private Set<Contributor> contributors;

	@Inject
	public TabulariusDesktop(ApplicationNameProperty applicationNameConfig) {
		super(applicationNameConfig);
	}
	
	public Set<Contributor> getContributors() {
		return contributors;
	}

	@Override
	protected void execInit() {
		setNavigationHandleVisible(false);
		setHeaderVisible(false);
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("WebContent/res/numbers").getFile());
			contributors = new NumbersFileWalker(file.getAbsolutePath()).getContributors();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void execDefaultView() {
		setOutline(TabulariusOutline.class);
	}

	@Order(10)
	public class NumbersOutlineViewButton extends AbstractOutlineViewButton {

		public NumbersOutlineViewButton() {
			this(TabulariusOutline.class);
		}

		protected NumbersOutlineViewButton(final Class<? extends TabulariusOutline> outlineClass) {
			super(TabulariusDesktop.this, outlineClass);
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_fileTextO;
		}
		
	    @Override
	    protected DisplayStyle getConfiguredDisplayStyle() {
	      return DisplayStyle.TAB;
	    }
	}

	public static class DesktopExtension extends AbstractDesktopExtension {

		@Override
		public void contributeOutlines(OrderedCollection<IOutline> outlines) {
			outlines.addAllLast(BEANS.all(IOutline.class));
		}
	}

}
