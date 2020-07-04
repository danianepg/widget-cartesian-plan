package com.danianepg.widget.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.exceptions.NotFoundException;
import com.danianepg.widget.exceptions.ValidationException;
import com.danianepg.widget.repositories.WidgetInMemoryRepository;
import com.danianepg.widget.repositories.WidgetRepository;
import com.danianepg.widget.repositories.WidgetRepositoryImpl;
import com.danianepg.widget.services.inmemory.WidgetPagingAndSortingService;

/**
 * Service to handle the business logic of Widgets.
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public class WidgetService {

	@Autowired
	private Environment environment;

	@Autowired
	private WidgetPagingAndSortingService pagingAndSorting;

	@Autowired
	private LocalValidatorFactoryBean validator;

	@Autowired
	private WidgetRepositoryImpl widgetRepositoryImpl;

	@Autowired
	private WidgetInMemoryRepository widgetInMemoryRepository;

	/**
	 * Find a widget by id.
	 *
	 * @param id
	 * @return
	 */
	public Widget findById(final Long id) {
		return this.getWidgetRepository().findById(id).orElseThrow(NotFoundException::new);
	}

	/**
	 * Find all widgets
	 *
	 * @param pageParam Return results paginated when informed or unpaged when
	 *                  null.
	 * @return
	 */
	public Page<Widget> findAll(final Pageable pageParam) {

		Pageable page = pageParam;
		if (page == null) {
			page = Pageable.unpaged();
		}

		return this.getWidgetRepository().findAll(page);
	}

	/**
	 * Validate if all mandatory attributes are filled and save a widget,
	 * rearranging the queue.
	 *
	 * @param widget
	 * @return
	 */
	public Widget save(@Valid final Widget widget) {

		final Errors errors = new BeanPropertyBindingResult(widget, "widget");
		this.validator.validate(widget, errors);

		if (errors.hasErrors() || errors.hasFieldErrors()) {
			throw new ValidationException(errors.toString());
		}

		return this.saveQueue(widget);
	}

	/**
	 * Validate if all mandatory attributes are filled and save a widget or create a
	 * new one in case the id is not found.
	 *
	 * @param widget
	 * @param id
	 * @return
	 */
	public Widget update(@Valid final Widget widget, final Long id) {

		return this.getWidgetRepository().findById(id).map(existingWidget -> {
			existingWidget.setX(widget.getX());
			existingWidget.setY(widget.getY());
			existingWidget.setZ(widget.getZ());
			existingWidget.setWidth(widget.getWidth());
			existingWidget.setHeight(widget.getHeight());
			return this.save(existingWidget);

		}).orElseGet(() -> this.save(widget));
	}

	/**
	 * Delete widget by id
	 *
	 * @param id
	 */
	public void deleteById(final Long id) {
		this.getWidgetRepository().deleteById(id);
	}

	/**
	 * Readjusts necessary z indexes and move the queue.
	 *
	 * @param widget
	 * @return
	 */
	private Widget saveQueue(final Widget widget) {

		if (Objects.isNull(widget.getZ())) {
			widget.setZ(this.fillZ(widget));
		}
		widget.setLastModification(LocalDateTime.now());

		final List<Widget> widgetsToMoveLst = this.moveQueue(widget);
		widgetsToMoveLst.add(widget);

		this.getWidgetRepository().saveAll(widgetsToMoveLst);

		return this.getWidgetRepository().findByZ(widget.getZ()).orElseGet(Widget::new);

	}

	/**
	 * Readjusts the queue
	 *
	 * @param newWidget
	 * @return
	 */
	private List<Widget> moveQueue(final Widget newWidget) {

		final List<Widget> widgetsToMoveLst = Collections.synchronizedList(new ArrayList<>());
		Long currentZ = newWidget.getZ();

		while (currentZ != null) {

			currentZ = this.getWidgetRepository().findByZ(currentZ).map(widgetAux -> {

				final Widget widgetCopy = widgetAux.clone();
				final Long auxZ = widgetCopy.getZ() + 1;
				widgetCopy.setZ(auxZ);
				widgetCopy.setLastModification(LocalDateTime.now());
				widgetsToMoveLst.add(widgetCopy);
				return auxZ;

			}).orElse(null);

		}

		return widgetsToMoveLst;

	}

	/**
	 * Fill z index with the greatest value plus one.
	 *
	 * @param widget
	 * @return
	 */
	private Long fillZ(final Widget widget) {

		final Pageable page = PageRequest.of(0, 1, Sort.by("z").descending());

		// @formatter:off
		final Widget latestWidget = this.getWidgetRepository().findAll(page)
				.getContent()
				.stream()
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(Widget::new);
		// @formatter:on

		if (latestWidget.getZ() == null) {
			return 1L;
		} else {
			return latestWidget.getZ() + 1;
		}
	}

	/**
	 * Filter if a widget is inside a filtered area. X and y on widgets indicate
	 * their center points. Based on that and its width and height, this method
	 * calculates if a widget is inside a desired area.
	 *
	 * @param lowerX   Position x to determine lower boundary
	 * @param lowerY   Position y to determine lower boundary
	 * @param upperX   Position x to determine upper boundary
	 * @param upperY   Position y to determine upper boundary
	 * @param pageable Return records paginated if informed or unpaged in case it is
	 *                 null.
	 * @return
	 */
	public Page<Widget> filterWidget(final Long lowerX, final Long lowerY, final Long upperX, final Long upperY,
			final Pageable pageable) {

		final List<Widget> widgetsFiltered = Collections.synchronizedList(new ArrayList<>());
		final List<Widget> widgets = this.getWidgetRepository().findAll(Pageable.unpaged()).getContent();

		for (final Widget w : widgets) {
			final Float left = w.getX() - w.getWidth() / 2;
			final Float right = w.getX() + w.getWidth() / 2;
			final Float bottom = w.getY() - w.getHeight() / 2;
			final Float top = w.getY() + w.getHeight() / 2;

			if (left >= lowerX && bottom >= lowerY && right <= upperX && top <= upperY) {
				widgetsFiltered.add(w);
			}
		}

		return this.pagingAndSorting.getPage(widgetsFiltered, pageable);
	}

	/**
	 * Delegate the repository to in memory or to SQL database according to the
	 * Spring profile informed on the initialization.
	 *
	 * @return
	 */
	private WidgetRepository getWidgetRepository() {

		final List<String> activeProfiles = Arrays.asList(this.environment.getActiveProfiles());
		if (activeProfiles.contains("db")) {
			return this.widgetRepositoryImpl;
		}

		return this.widgetInMemoryRepository;

	}

}
