package com.danianepg.widget.services.inmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.utils.ApplicationConstants;

/**
 * Paging and Sorting definition for widgets. It is applied when it is not
 * possible to
 * use Spring Paging and Sorting.
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public class WidgetPagingAndSortingService {

	/**
	 * Paginate a list of widgets.
	 *
	 * @param widgets
	 * @param pageableParm
	 * @return
	 */
	public PageImpl<Widget> getPage(final List<Widget> widgets, final Pageable pageableParm) {
		final List<Widget> widgetsCopy = Collections.synchronizedList(new ArrayList<>(widgets));
		return this.getPageImpl(pageableParm, widgetsCopy);
	}

	/**
	 * Paginate a map of widgets
	 *
	 * @param widgets
	 * @param pageableParm
	 * @return
	 */
	public PageImpl<Widget> getPage(final Map<Long, Widget> widgets, final Pageable pageableParm) {
		final List<Widget> widgetsCopy = widgets.values().stream().collect(Collectors.toList());
		return this.getPageImpl(pageableParm, widgetsCopy);
	}

	/**
	 * Slice an array an return it paginated and sorted.
	 *
	 * @param pageableParm
	 * @param widgetsCopy
	 * @return
	 */
	private PageImpl<Widget> getPageImpl(final Pageable pageableParm, List<Widget> widgetsCopy) {

		Pageable pageable = pageableParm;
		if (pageable == null || pageable.isUnpaged()) {
			int pageSize = widgetsCopy.size();
			if (pageSize <= 0) {
				pageSize = 1;
			}
			pageable = PageRequest.of(0, pageSize);
		}

		if (widgetsCopy == null || widgetsCopy.size() == 0) {
			return new PageImpl<Widget>(widgetsCopy, pageable, widgetsCopy.size());
		}

		final Long start = pageable.getOffset();
		final Long end = (start + pageable.getPageSize()) > widgetsCopy.size() ? widgetsCopy.size()
				: (start + pageable.getPageSize());

		final Sort sort = pageable.getSort();

		if (sort != null && sort.isSorted()) {
			widgetsCopy = this.getPageSorted(widgetsCopy, sort);
		}

		return new PageImpl<Widget>(widgetsCopy.subList(start.intValue(), end.intValue()), pageable,
				widgetsCopy.size());
	}

	/**
	 * Handle the sorting for all the {@link com.danianepg.widget.entities.Widget}
	 * attributes.
	 *
	 * @param widgets
	 * @param sort
	 * @return
	 */
	private List<Widget> getPageSorted(final List<Widget> widgets, final Sort sort) {

		Comparator<Widget> comparator = Comparator.comparing(Widget::getZ);
		boolean isAscending = false;

		final Order directionId = sort.getOrderFor("id");
		final Order directionX = sort.getOrderFor("x");
		final Order directionY = sort.getOrderFor("y");
		final Order directionZ = sort.getOrderFor("z");
		final Order directionWidth = sort.getOrderFor("width");
		final Order directionHeight = sort.getOrderFor("height");
		final Order directionlastModification = sort.getOrderFor("lastModification");

		if (directionId != null) {
			comparator = Comparator.comparing(Widget::getId);
			isAscending = directionId.getDirection().isAscending();

		} else if (directionX != null) {
			comparator = Comparator.comparing(Widget::getX);
			isAscending = directionX.getDirection().isAscending();

		} else if (directionY != null) {
			comparator = Comparator.comparing(Widget::getY);
			isAscending = directionY.getDirection().isAscending();

		} else if (directionZ != null) {
			comparator = Comparator.comparing(Widget::getZ);
			isAscending = directionZ.getDirection().isAscending();

		} else if (directionWidth != null) {
			comparator = Comparator.comparing(Widget::getWidth);
			isAscending = directionWidth.getDirection().isAscending();

		} else if (directionHeight != null) {
			comparator = Comparator.comparing(Widget::getHeight);
			isAscending = directionHeight.getDirection().isAscending();

		} else if (directionlastModification != null) {
			comparator = Comparator.comparing(Widget::getLastModification);
			isAscending = directionlastModification.getDirection().isAscending();
		}

		if (isAscending) {
			return widgets.stream().sorted(comparator).collect(Collectors.toList());
		} else {
			return widgets.stream().sorted(comparator.reversed()).collect(Collectors.toList());
		}

	}

	/**
	 * Get the default paging and sorting definitions.
	 *
	 * @param page
	 * @return
	 */
	public Pageable getPageAndSort(final Pageable page) {

		if (page == null) {
			return PageRequest.of(0, ApplicationConstants.PAGE_SIZE,
					Sort.by(ApplicationConstants.SORT_FIELD).descending());
		}

		return page;
	}

}
