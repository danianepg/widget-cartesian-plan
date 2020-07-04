package com.danianepg.widget.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Entity to represent the widget.
 *
 * @author Daniane P. Gomes
 *
 */
@Entity
public class Widget implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Id was defined as a Long instead of UUID to simplify the API calls.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * x and y represent the center point of the widget
	 */
	@NotNull
	private Long x;

	/**
	 * x and y represent the center point of the widget
	 */
	@NotNull
	private Long y;

	/**
	 * z represent the position of the widget on the plan. The greater the z index,
	 * the foremost is the widget on the plan.
	 */
	@Column(nullable = false)
	private Long z;

	/**
	 * Widget width size
	 */
	@NotNull
	@DecimalMin(value = "0")
	private Float width;

	/**
	 * Widget height size
	 */
	@NotNull
	@DecimalMin(value = "0")
	private Float height;

	/**
	 * Controlls the latest update on the widget.
	 */
	private LocalDateTime lastModification;

	public Widget() {
	}

	public Widget(final Long id) {
		this.id = id;
	}

	public Widget(@NotNull final Long x, @NotNull final Long y, final Long z,
			@NotNull @DecimalMin("0") final Float width, @NotNull @DecimalMin("0") final Float height) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
	}

	public Widget(final Long id, @NotNull final Long x, @NotNull final Long y, final Long z,
			@NotNull @DecimalMin("0") final Float width, @NotNull @DecimalMin("0") final Float height,
			final LocalDateTime lastModification) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.lastModification = lastModification;
	}

	public Widget(final Long id, @NotNull final Long x, @NotNull final Long y, final Long z,
			@NotNull @DecimalMin("0") final Float width, @NotNull @DecimalMin("0") final Float height) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
	}

	public Long getId() {
		return this.id;
	}

	public Long getX() {
		return this.x;
	}

	public void setX(final Long x) {
		this.x = x;
	}

	public Long getY() {
		return this.y;
	}

	public void setY(final Long y) {
		this.y = y;
	}

	public Long getZ() {
		return this.z;
	}

	public void setZ(final Long z) {
		this.z = z;
	}

	public Float getWidth() {
		return this.width;
	}

	public void setWidth(final Float width) {
		this.width = width;
	}

	public Float getHeight() {
		return this.height;
	}

	public void setHeight(final Float height) {
		this.height = height;
	}

	public LocalDateTime getLastModification() {
		return this.lastModification;
	}

	public void setLastModification(final LocalDateTime lastModification) {
		this.lastModification = lastModification;
	}

	@Override
	public Widget clone() {
		try {
			return (Widget) super.clone();
		} catch (final CloneNotSupportedException e) {
			return new Widget();
		}
	}

	@Override
	public String toString() {
		return "Widget [id=" + this.id + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", width=" + this.width
				+ ", height=" + this.height + ", lastModification=" + this.lastModification + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.height == null) ? 0 : this.height.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.lastModification == null) ? 0 : this.lastModification.hashCode());
		result = prime * result + ((this.width == null) ? 0 : this.width.hashCode());
		result = prime * result + ((this.x == null) ? 0 : this.x.hashCode());
		result = prime * result + ((this.y == null) ? 0 : this.y.hashCode());
		result = prime * result + ((this.z == null) ? 0 : this.z.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Widget other = (Widget) obj;
		if (this.height == null) {
			if (other.height != null) {
				return false;
			}
		} else if (!this.height.equals(other.height)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.lastModification == null) {
			if (other.lastModification != null) {
				return false;
			}
		} else if (!this.lastModification.equals(other.lastModification)) {
			return false;
		}
		if (this.width == null) {
			if (other.width != null) {
				return false;
			}
		} else if (!this.width.equals(other.width)) {
			return false;
		}
		if (this.x == null) {
			if (other.x != null) {
				return false;
			}
		} else if (!this.x.equals(other.x)) {
			return false;
		}
		if (this.y == null) {
			if (other.y != null) {
				return false;
			}
		} else if (!this.y.equals(other.y)) {
			return false;
		}
		if (this.z == null) {
			if (other.z != null) {
				return false;
			}
		} else if (!this.z.equals(other.z)) {
			return false;
		}
		return true;
	}

}
