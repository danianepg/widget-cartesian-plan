package com.danianepg.widget.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import com.danianepg.widget.controllers.WidgetController;
import com.danianepg.widget.entities.Widget;

/**
 * Assembler to convert entities to RESTful/HATEOAS format. Hyperlinks are added
 * to the
 * response and make the api navigable.
 *
 * @author Daniane P. Gomes
 *
 */
@Component
public class WidgetAssembler {

	/**
	 * Convert one widget object to HATEOAS RESTful/HATEOAS format.
	 * 
	 * @param widget
	 * @return
	 */
	public EntityModel<Widget> toEntityModel(final Widget widget) {
		final Link link = linkTo(WidgetController.class).slash(widget.getId()).withSelfRel();

		final EntityModel<Widget> entityModel = EntityModel.of(widget, link);
		entityModel.add(linkTo(methodOn(WidgetController.class).findAll(null)).withRel("widgets"));

		return entityModel;
	}

	/**
	 * Convert a list of widgets to RESTful/HATEOAS format.
	 * 
	 * @param widgetLst
	 * @param page
	 * @return
	 */
	public PagedModel<EntityModel<Widget>> toCollectionModel(final Page<Widget> widgetLst, final Pageable page) {

		final PagedModel.PageMetadata pageMetaData = new PagedModel.PageMetadata(page.getPageSize(),
				widgetLst.getNumber(), widgetLst.getTotalElements());

		final List<Widget> content = widgetLst.getContent();
		final List<EntityModel<Widget>> entityModels = content.stream().map(this::toEntityModel)
				.collect(Collectors.toList());

		final PagedModel<EntityModel<Widget>> widgets = PagedModel.of(entityModels, pageMetaData);
		widgets.add(linkTo(methodOn(WidgetController.class).findAll(page)).withSelfRel());

		widgets.add(linkTo(methodOn(WidgetController.class).findById(null)).withRel("findById"));
		widgets.add(linkTo(methodOn(WidgetController.class).filterByArea(null, null, null, null, null))
				.withRel("filterByArea"));

		return widgets;
	}

}
