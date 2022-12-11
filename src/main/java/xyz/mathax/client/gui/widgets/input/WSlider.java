package xyz.mathax.client.gui.widgets.input;

import xyz.mathax.client.gui.widgets.WWidget;
import xyz.mathax.client.utils.Utils;

public abstract class WSlider extends WWidget {
    public Runnable action;
    public Runnable actionOnRelease;

    protected double value;
    protected double min, max;

	// ghost slider for scrolling event
	protected double scrollHandleX, scrollHandleY, scrollHandleH;
	protected boolean scrollHandleMouseOver;

    protected boolean handleMouseOver;
    protected boolean dragging;
    protected double valueAtDragStart;

    public WSlider(double value, double min, double max) {
        this.value = Utils.clamp(value, min, max);
        this.min = min;
        this.max = max;
    }

    protected double handleSize() {
        return theme.textHeight();
    }

    @Override
    protected void onCalculateSize() {
        double handleSize = handleSize();

        width = handleSize;
        height = handleSize;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
        if (mouseOver && !used) {
            valueAtDragStart = value;
            double handleSize = handleSize();

            double valueWidth = mouseX - (x + handleSize / 2);
            set((valueWidth / (width - handleSize)) * (max - min) + min);
            if (action != null) {
                action.run();
            }

            dragging = true;
            return true;
        }

        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
        double valueWidth = valueWidth();
        double handleSize = handleSize();
        double handleSize2 = handleSize / 2;

        double x = this.x + handleSize2 + valueWidth - height / 2;
        handleMouseOver =  mouseX >= x && mouseX <= x + height && mouseY >= y && mouseY <= y + height;

		if (!scrollHandleMouseOver) {
			scrollHandleX = x;
			scrollHandleY = y;
			scrollHandleH = height;
			if (handleMouseOver) {
				scrollHandleMouseOver = true;
			}
		} else {
			scrollHandleMouseOver = mouseX >= scrollHandleX && mouseX <= scrollHandleX + scrollHandleH && mouseY >= scrollHandleY && mouseY <= scrollHandleY + scrollHandleH;
		}

        boolean mouseOverX = mouseX >= this.x + handleSize2 && mouseX <= this.x + handleSize2 + width - handleSize;
        mouseOver = mouseOverX && mouseY >= this.y && mouseY <= this.y + height;

        if (dragging) {
            if (mouseOverX) {
                valueWidth += mouseX - lastMouseX;
                valueWidth = Utils.clamp(valueWidth, 0, width - handleSize);

                set((valueWidth / (width - handleSize)) * (max - min) + min);
                if (action != null) {
                    action.run();
                }
            } else {
                if (value > min && mouseX < this.x + handleSize2) {
                    value = min;
                    if (action != null) {
                        action.run();
                    }
                } else if (value < max && mouseX > this.x + handleSize2 + width - handleSize) {
                    value = max;
                    if (action != null) {
                        action.run();
                    }
                }
            }
        }
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            if (value != valueAtDragStart && actionOnRelease != null) {
                actionOnRelease.run();
            }

            dragging = false;
            return true;
        }

        return false;
    }

	@Override
	public boolean onMouseScrolled(double amount) {
		// when user starts to scroll over regular handle
		// remember it's position and check only this "ghost"
		// position to allow scroll (until it leaves ghost area)
		if (!scrollHandleMouseOver && handleMouseOver) {
			scrollHandleX = x;
			scrollHandleY = y;
			scrollHandleH = height;
			scrollHandleMouseOver = true;
		}

		if (scrollHandleMouseOver) {
			if (parent instanceof WIntEdit) {
				set(value + amount);
			} else {
				set(value + 0.05 * amount);
			}

			if (action != null) {
                action.run();
            }

			return true;
		}

		return false;
	}

    public void set(double value) {
        this.value = Utils.clamp(value, min, max);
    }

    public double get() {
        return value;
    }

    protected double valueWidth() {
        double valuePercentage = (value - min) / (max - min);
        return valuePercentage * (width - handleSize());
    }
}