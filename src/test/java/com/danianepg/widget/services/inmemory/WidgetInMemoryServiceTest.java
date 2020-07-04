package com.danianepg.widget.services.inmemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.exceptions.NotFoundException;
import com.danianepg.widget.exceptions.ValidationException;
import com.danianepg.widget.services.WidgetService;
import com.danianepg.widget.util.WidgetMock;
import com.danianepg.widget.utils.ApplicationConstants;

@ActiveProfiles(profiles = { "inmemory", "default" })
@RunWith(SpringRunner.class)
@SpringBootTest
public class WidgetInMemoryServiceTest {

	@Autowired
	private WidgetService service;

	@MockBean
	private WidgetsInMemoryStorageService widgets;

	@Test
	public void findById_ok() {
		when(this.widgets.getWidgets()).thenReturn(WidgetMock.getWidgetsMock());
		assertThat(this.service.findById(1L)).isNotNull();
	}

	@Test(expected = NotFoundException.class)
	public void findById_throwExceptionWhenNotFound() {
		when(this.widgets.getWidgets()).thenReturn(WidgetMock.getWidgetsMock());
		this.service.findById(20L);
	}

	@Test
	public void findAll_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		assertTrue(this.service.findAll(null).hasContent());
		assertThat(this.service.findAll(null).getTotalElements()).isEqualTo(widgets.size());

	}

	@Test
	public void findAll_emptyList() {
		when(this.widgets.getWidgets()).thenReturn(new ConcurrentHashMap<>());
		assertFalse(this.service.findAll(null).hasContent());
		assertThat(this.service.findAll(null)).isInstanceOf(Page.class);
	}

	@Test
	public void save_ok() {

		when(this.widgets.getWidgets()).thenCallRealMethod();

		// ID 1 - Position z = null becomes 1
		Widget widget1 = new Widget(2L, 1L, null, 10f, 11111f);
		widget1 = this.service.save(widget1);

		// ID 2 - Position z = 6
		Widget widget2 = new Widget(1L, 2L, 6L, 10f, 10f);
		widget2 = this.service.save(widget2);

		// ID 3 - Position z = 7
		Widget widget3 = new Widget(1L, 2L, 7L, 10f, 10f);
		widget3 = this.service.save(widget3);

		// ID 4 - Position z = 10
		Widget widget4 = new Widget(1L, 2L, 10L, 10f, 10f);
		widget4 = this.service.save(widget4);

		// ID 5 - Position z = 6. Move id 2 to position z = 7 and id 3 to position z = 8
		Widget widget5 = new Widget(1L, 2L, 6L, 10f, 10f);
		widget5 = this.service.save(widget5);

		// ID 6 - Position z = null. It will be filled with z = 11
		Widget widget6 = new Widget(1L, 2L, null, 10f, 10f);
		widget6 = this.service.save(widget6);

		final Pageable page = PageRequest.of(0, 20, Sort.by("id"));
		final List<Widget> savedWidgets = this.service.findAll(page).getContent();

		assertThat(savedWidgets.get(0).getId()).isEqualTo(1L);
		assertThat(savedWidgets.get(0).getZ()).isEqualTo(1L);

		assertThat(savedWidgets.get(1).getId()).isEqualTo(2L);
		assertThat(savedWidgets.get(1).getZ()).isEqualTo(7L);

		assertThat(savedWidgets.get(2).getId()).isEqualTo(3L);
		assertThat(savedWidgets.get(2).getZ()).isEqualTo(8L);

		assertThat(savedWidgets.get(3).getId()).isEqualTo(4L);
		assertThat(savedWidgets.get(3).getZ()).isEqualTo(widget4.getZ());

		assertThat(savedWidgets.get(4).getId()).isEqualTo(5L);
		assertThat(savedWidgets.get(4).getZ()).isEqualTo(widget5.getZ());

		assertThat(savedWidgets.get(5).getId()).isEqualTo(6L);
		assertThat(savedWidgets.get(5).getZ()).isEqualTo(11L);
	}

	@Test(expected = ValidationException.class)
	public void save_failWhenFieldsAreInvalid() {

		when(this.widgets.getWidgets()).thenCallRealMethod();
		final Widget widget = new Widget();
		this.service.save(widget);

	}

	@Test
	public void filterWidget_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		final Pageable page1 = PageRequest.of(0, 1, Sort.by(ApplicationConstants.SORT_FIELD).descending());
		final Page<Widget> filteredPage1 = this.service.filterWidget(0L, 0L, 100L, 150L, page1);

		final Pageable page2 = PageRequest.of(1, 1, Sort.by(ApplicationConstants.SORT_FIELD).descending());
		final Page<Widget> filteredPage2 = this.service.filterWidget(0L, 0L, 100L, 150L, page2);

		assertThat(filteredPage1.getTotalElements()).isGreaterThan(0L);
		assertThat(filteredPage1.getContent().get(0).getId()).isEqualTo(widgets.get(6L).getId());

		assertThat(filteredPage2.getTotalElements()).isGreaterThan(0L);
		assertThat(filteredPage2.getContent().get(0).getId()).isEqualTo(widgets.get(5L).getId());

	}

	@Test
	public void filterWidget_notFound() {
		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);
		assertThat(this.service.filterWidget(100L, 0L, 150L, 50L, null).getTotalElements()).isEqualTo(0L);

	}

}
