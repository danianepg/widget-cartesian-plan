package com.danianepg.widget.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.danianepg.widget.assemblers.WidgetAssembler;
import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.exceptions.HateosMapperException;
import com.danianepg.widget.services.WidgetService;
import com.danianepg.widget.services.inmemory.WidgetPagingAndSortingService;

/**
 * Controller to handle widget API.
 *
 * @author Daniane P. Gomes
 *
 */
@RestController
@RequestMapping("/api/widgets")
public class WidgetController {

	@Autowired
	private WidgetService widgetService;

	@Autowired
	private WidgetAssembler assembler;

	@Autowired
	private WidgetPagingAndSortingService pagingAndSorting;

	/**
	 * Find a widget by its id.
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public EntityModel<Widget> findById(@PathVariable final Long id) {
		return Optional.of(this.widgetService.findById(id)).map(this.assembler::toEntityModel)
				.orElseThrow(HateosMapperException::new);
	}

	/**
	 * Create a widget
	 *
	 * @param widget
	 * @return
	 */
	@PostMapping("")
	public EntityModel<Widget> create(@RequestBody final Widget widget) {
		return Optional.of(this.widgetService.save(widget)).map(this.assembler::toEntityModel)
				.orElseThrow(HateosMapperException::new);
	}

	/**
	 * Update a widget or create a new one if it does not exists, in compliance to
	 * PUT idempotency characteristics.
	 *
	 * @param widget
	 * @param id
	 * @return
	 */
	@PutMapping("/{id}")
	public EntityModel<Widget> update(@RequestBody final Widget widget, @PathVariable final Long id) {
		return Optional.of(this.widgetService.update(widget, id)).map(this.assembler::toEntityModel)
				.orElseThrow(HateosMapperException::new);
	}

	/**
	 * Delete widget by id
	 *
	 * @param id
	 */
	@DeleteMapping("/{id}")
	public void delete(@PathVariable final Long id) {
		this.widgetService.deleteById(id);
	}

	/**
	 * Find all widgets paginated.
	 *
	 * @param page
	 * @return
	 */
	@GetMapping("")
	public PagedModel<EntityModel<Widget>> findAll(final Pageable page) {
		final Pageable pageRequest = this.pagingAndSorting.getPageAndSort(page);

		return Optional.of(this.widgetService.findAll(pageRequest))
				.map(p -> this.assembler.toCollectionModel(p, pageRequest)).orElseThrow(HateosMapperException::new);

	}

	/**
	 * Filter widgets by a determined area and return paginated results.
	 *
	 * @param lowerX Position x to determine lower boundary
	 * @param lowerY Position y to determine lower boundary
	 * @param upperX Position x to determine upper boundary
	 * @param upperY Position y to determine upper boundary
	 * @param page
	 * @return
	 */
	@GetMapping("/filter")
	public PagedModel<EntityModel<Widget>> filterByArea(@RequestParam("lowerX") final Long lowerX,
			@RequestParam("lowerY") final Long lowerY, @RequestParam("upperX") final Long upperX,
			@RequestParam("upperY") final Long upperY, final Pageable page) {

		final Pageable pageRequest = this.pagingAndSorting.getPageAndSort(page);

		return Optional.of(this.widgetService.filterWidget(lowerX, lowerY, upperX, upperY, pageRequest))
				.map(p -> this.assembler.toCollectionModel(p, pageRequest)).orElseThrow(HateosMapperException::new);

	}

}
