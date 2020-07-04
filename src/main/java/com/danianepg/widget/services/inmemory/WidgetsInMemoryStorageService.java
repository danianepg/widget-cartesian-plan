package com.danianepg.widget.services.inmemory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.danianepg.widget.entities.Widget;

/**
 * Auxiliary class to handle in memory widgets. Keep it separated to facilitate
 * mocks on tests.
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public class WidgetsInMemoryStorageService {

	private Map<Long, Widget> widgets;

	public Map<Long, Widget> getWidgets() {

		if (this.widgets == null) {
			this.widgets = new ConcurrentHashMap<>();
		}

		return this.widgets;
	}

	public Map<Long, Widget> deleteAll() {
		this.widgets = new ConcurrentHashMap<>();
		return this.widgets;
	}

}
