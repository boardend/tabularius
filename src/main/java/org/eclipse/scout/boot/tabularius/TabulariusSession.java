package org.eclipse.scout.boot.tabularius;

import java.util.UUID;

import org.eclipse.scout.boot.ui.AbstractSpringBootClientSession;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;

@Bean
public class TabulariusSession extends AbstractSpringBootClientSession {

	public static TabulariusSession get() {
		return ClientSessionProvider.currentSession(TabulariusSession.class);
	}

	@Override
	protected void execLoadSession() {
		setDesktop(BEANS.get(TabulariusDesktop.class));
	}

	@Override
	public String getUserId() {
		return UUID.randomUUID().toString();
	}
}
