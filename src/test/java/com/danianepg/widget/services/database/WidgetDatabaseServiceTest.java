package com.danianepg.widget.services.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
import com.danianepg.widget.repositories.WidgetDatabaseRepository;
import com.danianepg.widget.services.WidgetService;
import com.danianepg.widget.util.WidgetMock;
import com.danianepg.widget.utils.ApplicationConstants;

@ActiveProfiles(profiles = "db")
@RunWith(SpringRunner.class)
@SpringBootTest
public class WidgetDatabaseServiceTest {

	@Autowired
	private WidgetService service;

	@MockBean
	private WidgetDatabaseRepository widgetRepository;

	@Test
	public void findById_ok() {

		final Widget widgetMock = WidgetMock.getWidgetsMock().get(1L);
		when(this.widgetRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(widgetMock));
		assertThat(this.service.findById(1L)).isNotNull();
	}

	@Test(expected = NotFoundException.class)
	public void findById_throwExceptionWhenNotFound() {
		when(this.widgetRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		this.service.findById(20L);
	}

	@Test
	public void findAll_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgetRepository.findAll(ArgumentMatchers.nullable(Pageable.class)))
				.thenReturn(WidgetMock.getWidgetsPaged(widgets, null));

		assertTrue(this.service.findAll(null).hasContent());
		assertThat(this.service.findAll(null).getTotalElements()).isEqualTo(widgets.size());

	}

	@Test
	public void findAll_emptyList() {

		final Map<Long, Widget> widgets = new ConcurrentHashMap<>();
		when(this.widgetRepository.findAll(ArgumentMatchers.nullable(Pageable.class)))
				.thenReturn(WidgetMock.getWidgetsPaged(widgets, null));

		assertFalse(this.service.findAll(null).hasContent());
		assertThat(this.service.findAll(null)).isInstanceOf(Page.class);
	}

	@Test
	public void save_ok() {

		final Map<Long, Widget> widgets = new ConcurrentHashMap<>();

		when(this.widgetRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenAnswer(invocation -> {
			final Pageable page = invocation.getArgument(0);
			return WidgetMock.getWidgetsPaged(widgets, page);
		});

		when(this.widgetRepository.findByZ(ArgumentMatchers.anyLong())).thenAnswer(invocation -> {

			final Long z = invocation.getArgument(0);

			// @formatter:off
			final Optional<Widget> widgetZ = widgets.entrySet()
				.stream()
				.map(Entry::getValue)
				.filter(w -> w.getZ() == z)
				.findFirst();
			// @formatter:on

			return widgetZ;

		});

		when(this.widgetRepository.saveAll(ArgumentMatchers.anyCollection())).thenAnswer(invocation -> {

			@SuppressWarnings("unchecked")
			final List<Widget> saved = (List<Widget>) invocation.getArgument(0);

			saved.stream().forEach(w -> {
				widgets.put(w.getId(), w);
			});
			return saved;
		});

		// ID 1 - Position z = null becomes 1
		Widget widget1 = new Widget(1L, 2L, 1L, null, 10f, 10f, LocalDateTime.now());
		widget1 = this.service.save(widget1);

		// ID 2 - Position z = 6
		Widget widget2 = new Widget(2L, 1L, 2L, 6L, 10f, 10f, LocalDateTime.now());
		widget2 = this.service.save(widget2);

		// ID 3 - Position z = 7
		Widget widget3 = new Widget(3L, 1L, 2L, 7L, 10f, 10f, LocalDateTime.now());
		widget3 = this.service.save(widget3);

		// ID 4 - Position z = 10
		Widget widget4 = new Widget(4L, 1L, 2L, 10L, 10f, 10f, LocalDateTime.now());
		widget4 = this.service.save(widget4);

		// ID 5 - Position z = 6. Move id 2 to position z = 7 and id 3 to position z = 8
		Widget widget5 = new Widget(5L, 1L, 2L, 6L, 10f, 10f, LocalDateTime.now());
		widget5 = this.service.save(widget5);

		// ID 6 - Position z = null. It will be filled with z = 11
		Widget widget6 = new Widget(6L, 1L, 2L, null, 10f, 10f, LocalDateTime.now());
		widget6 = this.service.save(widget6);

		assertThat(widgets.get(1L).getZ()).isEqualTo(1L);
		assertThat(widgets.get(2L).getZ()).isEqualTo(widget2.getZ() + 1L);
		assertThat(widgets.get(3L).getZ()).isEqualTo(widget3.getZ() + 1L);
		assertThat(widgets.get(4L).getZ()).isEqualTo(widget4.getZ());
		assertThat(widgets.get(5L).getZ()).isEqualTo(widget5.getZ());
		assertThat(widgets.get(6L).getZ()).isEqualTo(11L);

	}

	@Test(expected = ValidationException.class)
	public void save_failWhenFieldsAreInvalid() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgetRepository.findAll(ArgumentMatchers.nullable(Pageable.class)))
				.thenReturn(WidgetMock.getWidgetsPaged(widgets, null));

		final Widget widget = new Widget();
		this.service.save(widget);

	}

	@Test
	public void update_ok() {

		final Map<Long, Widget> widgets = new ConcurrentHashMap<>();

		final Widget widgetMock = WidgetMock.getWidgetsMock().get(1L);
		when(this.widgetRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(widgetMock));

		when(this.widgetRepository.saveAll(ArgumentMatchers.anyCollection())).thenAnswer(invocation -> {

			@SuppressWarnings("unchecked")
			final List<Widget> saved = (List<Widget>) invocation.getArgument(0);

			saved.stream().forEach(w -> {
				widgets.put(w.getId(), w);
			});
			return saved;
		});

		final Long id = 1L;
		final Long x = 200L;
		final Long y = 300L;
		final Long z = 400L;
		final Float width = 500f;
		final Float height = 500f;
		final LocalDateTime current = LocalDateTime.now();

		final Widget updatedWidget = new Widget(id);
		updatedWidget.setX(x);
		updatedWidget.setY(y);
		updatedWidget.setZ(z);
		updatedWidget.setWidth(width);
		updatedWidget.setHeight(height);

		this.service.update(updatedWidget, id);

		assertThat(widgets.get(1L).getId()).isEqualTo(id);
		assertThat(widgets.get(1L).getX()).isEqualTo(x);
		assertThat(widgets.get(1L).getY()).isEqualTo(y);
		assertThat(widgets.get(1L).getZ()).isEqualTo(z);
		assertThat(widgets.get(1L).getWidth()).isEqualTo(width);
		assertThat(widgets.get(1L).getHeight()).isEqualTo(height);
		assertThat(widgets.get(1L).getLastModification().isAfter(current));

	}

	@Test
	public void update_createNewObjectWhenIdNoExists() {

		final Map<Long, Widget> widgets = new ConcurrentHashMap<>();

		when(this.widgetRepository.saveAll(ArgumentMatchers.anyCollection())).thenAnswer(invocation -> {

			@SuppressWarnings("unchecked")
			final List<Widget> saved = (List<Widget>) invocation.getArgument(0);

			saved.stream().forEach(w -> {
				widgets.put(w.getId(), w);
			});
			return saved;
		});

		final Long id = 2L;
		final Long x = 200L;
		final Long y = 300L;
		final Long z = 400L;
		final Float width = 500f;
		final Float height = 500f;

		final Widget updatedWidget = new Widget(id);
		updatedWidget.setX(x);
		updatedWidget.setY(y);
		updatedWidget.setZ(z);
		updatedWidget.setWidth(width);
		updatedWidget.setHeight(height);

		this.service.update(updatedWidget, id);
		assertThat(widgets.get(id)).isInstanceOf(Widget.class);

	}

	@Test
	public void filterWidget_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgetRepository.findAll(ArgumentMatchers.nullable(Pageable.class)))
				.thenReturn(WidgetMock.getWidgetsPaged(widgets, null));

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
		when(this.widgetRepository.findAll(ArgumentMatchers.nullable(Pageable.class)))
				.thenReturn(WidgetMock.getWidgetsPaged(widgets, null));
		assertTrue(this.service.filterWidget(100L, 0L, 150L, 50L, null).getTotalElements() == 0);

	}

}
