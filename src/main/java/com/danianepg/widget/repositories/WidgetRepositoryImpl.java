package com.danianepg.widget.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.danianepg.widget.entities.Widget;

/**
 * Implementation of {@link com.danianepg.widget.repositories.WidgetRepository}
 * to inject Spring Repository
 * {@link com.danianepg.widget.repositories.WidgetDatabaseRepository} and allow
 * application to decide in runtime to where delegate the storage, according the
 * active profile.
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public class WidgetRepositoryImpl implements WidgetRepository {

	@Autowired
	private WidgetDatabaseRepository widgetDatabaseRepository;

	@Override
	public Optional<Widget> findById(final Long id) {
		return this.widgetDatabaseRepository.findById(id);
	}

	@Override
	public void deleteById(final Long id) {
		this.widgetDatabaseRepository.deleteById(id);
	}

	@Override
	public Page<Widget> findAll(final Pageable pageable) {
		return this.widgetDatabaseRepository.findAll(pageable);
	}

	@Override
	public Map<Long, Widget> saveAll(final List<Widget> widgetsToMoveLst) {

		final Iterable<Widget> saved = this.widgetDatabaseRepository.saveAll(widgetsToMoveLst);
		final Map<Long, Widget> map = new ConcurrentHashMap<>();

		saved.forEach(w -> map.put(w.getId(), w));

		return map;
	}

	@Override
	public Optional<Widget> findByZ(final Long currentZ) {
		return this.widgetDatabaseRepository.findByZ(currentZ);
	}

	@Override
	public Widget save(@Valid final Widget widget) {
		return this.widgetDatabaseRepository.save(widget);
	}

}
