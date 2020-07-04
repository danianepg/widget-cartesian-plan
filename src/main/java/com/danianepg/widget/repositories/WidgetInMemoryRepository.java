package com.danianepg.widget.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.exceptions.NotFoundException;
import com.danianepg.widget.exceptions.ValidationException;
import com.danianepg.widget.services.inmemory.WidgetPagingAndSortingService;
import com.danianepg.widget.services.inmemory.WidgetsInMemoryStorageService;

/**
 * In memory repository to handle {@link com.danianepg.widget.entities.Widget}
 * entities.
 *
 * @author Daniane P. Gomes
 *
 */
@Repository
public class WidgetInMemoryRepository implements WidgetRepository {

	/**
	 * AtomicLong id to handle atomic requests and leverage concurrence problems.
	 */
	private final AtomicLong latestId = new AtomicLong();

	@Autowired
	private WidgetsInMemoryStorageService widgetsStored;

	@Autowired
	private WidgetPagingAndSortingService pagingAndSorting;

	@Autowired
	private LocalValidatorFactoryBean validator;

	/**
	 * Find a widget by id and return an Optional value of it.
	 */
	@Override
	public Optional<Widget> findById(final Long id) {
		return Optional.ofNullable(this.widgetsStored.getWidgets().get(id));
	}

	/**
	 * Delete a widget by id.
	 */
	@Override
	public void deleteById(final Long id) {

		final Widget widget = this.widgetsStored.getWidgets().remove(id);
		if (widget == null) {
			throw new NotFoundException();
		}
	}

	/**
	 * Find all widgets paginated.
	 */
	@Override
	public Page<Widget> findAll(final Pageable pageable) {

		final Map<Long, Widget> widgetsCopy = new ConcurrentHashMap<>(this.widgetsStored.getWidgets());
		return this.pagingAndSorting.getPage(widgetsCopy, pageable);

	}

	/**
	 * Save all the widgets adjusting the queue as needed. This method does not lock
	 * the operations, thus it keeps only the latest values informed to the widgets.
	 *
	 * The tradeoff was to lock the whole transaction or always replace the values
	 * in case of concurrent access. The approach adopted was to only keep the
	 * latest value and do not lock the Web application.
	 *
	 * @param widgetsToMoveLst The whole list of widgets with z indexes already
	 *                         adjusted.
	 * @return
	 */

	@Override
	public Map<Long, Widget> saveAll(final List<Widget> widgetsToMoveLst) {
		widgetsToMoveLst.stream().forEach(wParam -> {

			final Widget w;

			if (wParam.getId() == null) {
				w = new Widget(this.getLatestId());
				w.setX(wParam.getZ());
				w.setY(wParam.getY());
				w.setZ(wParam.getZ());
				w.setWidth(wParam.getWidth());
				w.setHeight(wParam.getHeight());
			} else {
				w = wParam;
			}

			w.setLastModification(LocalDateTime.now());
			this.widgetsStored.getWidgets().compute(w.getId(), (key, oldValue) -> w);
		});

		return this.widgetsStored.getWidgets();
	}

	/**
	 * Handle the creation of the widgets ids.
	 *
	 * @return
	 */
	private Long getLatestId() {
		return this.latestId.incrementAndGet();
	}

	/**
	 * Find a widget by its z index
	 */
	@Override
	public Optional<Widget> findByZ(final Long currentZ) {

		final Map<Long, Widget> widgetsCopy = new ConcurrentHashMap<>(this.widgetsStored.getWidgets());

		// @formatter:off
		return widgetsCopy.values()
				.stream()
				.filter(Objects::nonNull )
				.filter(w-> Objects.nonNull(w.getZ()))
				.filter(w -> w.getZ().equals(currentZ))
				.findFirst();
		// @formatter:on

	}

	/**
	 * Save one widget.
	 */
	@Override
	public Widget save(@Valid final Widget widget) {
		final List<Widget> widgets = Collections.synchronizedList(new ArrayList<>());
		widgets.add(widget);

		final Errors errors = new BeanPropertyBindingResult(widget, "widget");
		this.validator.validate(widget, errors);

		if (errors.hasErrors() || errors.hasFieldErrors()) {
			throw new ValidationException(errors.toString());
		}

		return this.saveAll(widgets).get(widget.getId());

	}

}
