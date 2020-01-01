package io.golos.cyber_android.ui.shared.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.golos.cyber_android.R;

public class RoundedView extends FrameLayout {

    private float topLeftCornerRadius;
    private float topRightCornerRadius;
    private float bottomLeftCornerRadius;
    private float bottomRightCornerRadius;

    public RoundedView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.RoundedView,
                0,
                0
        );

        topLeftCornerRadius = typedArray.getDimension(R.styleable.RoundedView_topLeftCornerRadius, 0);
        topRightCornerRadius = typedArray.getDimension(R.styleable.RoundedView_topRightCornerRadius, 0);
        bottomLeftCornerRadius = typedArray.getDimension(R.styleable.RoundedView_bottomLeftCornerRadius, 0);
        bottomRightCornerRadius = typedArray.getDimension(R.styleable.RoundedView_bottomRightCornerRadius, 0);

        typedArray.recycle();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();

        float[] cornerDimensions = {
                topLeftCornerRadius, topLeftCornerRadius,
                topRightCornerRadius, topRightCornerRadius,
                bottomRightCornerRadius, bottomRightCornerRadius,
                bottomLeftCornerRadius, bottomLeftCornerRadius
        };

        path.addRoundRect(
                new RectF(0, 0, canvas.getWidth(), canvas.getHeight()),
                cornerDimensions,
                Path.Direction.CW
        );

        canvas.clipPath(path);

        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
    }

}
