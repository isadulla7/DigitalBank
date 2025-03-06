package uz.fido.utils.view.imagezoomcrop.cropoverlay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import uz.fido.utils.R;
import uz.fido.utils.view.imagezoomcrop.cropoverlay.edge.Edge;
import uz.fido.utils.view.imagezoomcrop.cropoverlay.utils.PaintUtil;
import uz.fido.utils.view.imagezoomcrop.photoview.IGetImageBounds;

/**
 * @author GT Modified/stripped down Code from cropper library : https://github.com/edmodo/cropper
 */
public class CropOverlayView extends View implements IGetImageBounds {

    // we are cropping square image so width and height will always be equal
    private final int DEFAULT_CROPWIDTH = 600;
    private static final int DEFAULT_CORNER_RADIUS = 6;
    private static final int DEFAULT_OVERLAY_COLOR = Color.argb(204, 41, 48, 63);

    // The Paint used to darken the surrounding areas outside the crop area.
    private Paint mBackgroundPaint;

    // The Paint used to draw the white rectangle around the crop area.
    private Paint mBorderPaint;

    // The Paint used to draw the guidelines within the crop area.
    private Paint mGuidelinePaint;

    private Path mClipPath;

    // The bounding box around the Bitmap that we are cropping.
    private RectF mBitmapRect;

    private int cropHeight = DEFAULT_CROPWIDTH;
    private int cropWidth = DEFAULT_CROPWIDTH;


    private boolean mGuidelines;
    private int mMarginTop;
    private int mMarginSide;
    private int mMinWidth;
    private int mMaxWidth;
    private int mCornerRadius;
    private int mOverlayColor;
    private final Context mContext;

    public CropOverlayView(Context context) {
        super(context);
        init(context);
        mContext = context;
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropOverlayView, 0, 0);
        try {
            //Defaults
            boolean DEFAULT_GUIDELINES = true;
            mGuidelines = ta.getBoolean(R.styleable.CropOverlayView_guideLines, DEFAULT_GUIDELINES);
            int DEFAULT_MARGINTOP = 100;
            mMarginTop = ta.getDimensionPixelSize(R.styleable.CropOverlayView_marginTop, DEFAULT_MARGINTOP);
            int DEFAULT_MARGINSIDE = 50;
            mMarginSide = ta.getDimensionPixelSize(R.styleable.CropOverlayView_marginSide, DEFAULT_MARGINSIDE);
            int DEFAULT_MIN_WIDTH = 500;
            mMinWidth = ta.getDimensionPixelSize(R.styleable.CropOverlayView_minWidth, DEFAULT_MIN_WIDTH);
            int DEFAULT_MAX_WIDTH = 700;
            mMaxWidth = ta.getDimensionPixelSize(R.styleable.CropOverlayView_maxWidth, DEFAULT_MAX_WIDTH);
            final float defaultRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CORNER_RADIUS, mContext.getResources().getDisplayMetrics());
            mCornerRadius = ta.getDimensionPixelSize(R.styleable.CropOverlayView_cornersRadius, (int) defaultRadius);
            mOverlayColor = ta.getColor(R.styleable.CropOverlayView_overlayColor, DEFAULT_OVERLAY_COLOR);
        } finally {
            ta.recycle();
        }

        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        canvas.save();
        mBitmapRect.left = Edge.LEFT.getCoordinate();
        mBitmapRect.top = Edge.TOP.getCoordinate();
        mBitmapRect.right = Edge.RIGHT.getCoordinate();
        mBitmapRect.bottom = Edge.BOTTOM.getCoordinate();

        mClipPath.addRoundRect(mBitmapRect, mCornerRadius, mCornerRadius, Path.Direction.CW);
        canvas.clipPath(mClipPath, Region.Op.DIFFERENCE);
        canvas.drawColor(mOverlayColor);
        mClipPath.reset();
        canvas.restore();
        canvas.drawRoundRect(mBitmapRect, mCornerRadius, mCornerRadius, mBorderPaint);

        if (mGuidelines) {
            drawRuleOfThirdsGuidelines(canvas);
        }
    }

    @Override
    public Rect getImageBounds() {
        return new Rect(
                (int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(),
                (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
    }

    // Private Methods /////////////////////////////////////////////////////////
    private void init(Context context) {
        int w = context.getResources().getDisplayMetrics().widthPixels;
        cropWidth = w - 2 * mMarginSide;
        //noinspection SuspiciousNameCombination
        cropHeight = cropWidth;
        int edgeT = mMarginTop;
        int edgeB = mMarginTop + cropHeight;
        int edgeL = mMarginSide;
        int edgeR = mMarginSide + cropWidth;
        mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
        mBorderPaint = PaintUtil.newBorderPaint(context);
        mGuidelinePaint = PaintUtil.newGuidelinePaint();
        Edge.TOP.setCoordinate(edgeT);
        Edge.BOTTOM.setCoordinate(edgeB);
        Edge.LEFT.setCoordinate(edgeL);
        Edge.RIGHT.setCoordinate(edgeR);
        mBitmapRect = new RectF(edgeL, edgeT, edgeR, edgeB);
        mClipPath = new Path();
    }


    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        // Draw vertical guidelines.
        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        // Draw horizontal guidelines.
        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

}