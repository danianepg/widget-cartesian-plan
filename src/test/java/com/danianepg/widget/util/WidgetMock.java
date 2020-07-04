package com.danianepg.widget.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.services.inmemory.WidgetPagingAndSortingService;

public class WidgetMock {

	public static Map<Long, Widget> getWidgetsMock() {

		final Map<Long, Widget> widgets = new ConcurrentHashMap<>();

		Widget widget = new Widget(1L, 2L, 1L, 1L, 10f, 10f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(2L, 1L, 2L, 6L, 10f, 10f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(3L, 1L, 2L, 7L, 10f, 10f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(4L, 1L, 2L, 10L, 10f, 10f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(5L, 50L, 50L, 11L, 100f, 100f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(6L, 50L, 100L, 12L, 100f, 100f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		widget = new Widget(7L, 100L, 150L, 13L, 100f, 100f, LocalDateTime.now());
		widgets.put(widget.getId(), widget);

		return widgets;

	}

	public static Page<Widget> getWidgetsPaged(final Map<Long, Widget> widgets, final Pageable pageParam) {
		final WidgetPagingAndSortingService pagingAndSorting = new WidgetPagingAndSortingService();

		Pageable page = pageParam;
		if (page == null) {
			page = Pageable.unpaged();
		}

		return pagingAndSorting.getPage(widgets, page);
	}

	public static Widget getWidget() {

		final Random random = new Random();

		final Long x = random.longs(1, 500).findFirst().getAsLong();
		final Long y = random.longs(1, 500).findFirst().getAsLong();
		final Long z = random.longs(1, 500).findFirst().getAsLong();
		final Float width = (float) random.doubles(1, 500).findFirst().getAsDouble();
		final Float height = (float) random.doubles(1, 500).findFirst().getAsDouble();

		final Widget widget = new Widget();
		widget.setX(x);
		widget.setY(y);
		widget.setZ(z);
		widget.setWidth(width);
		widget.setHeight(height);

		return widget;
	}

}
