package uz.fido.utils.view.custom_edit_text.mask_edit_text;

public interface DrawableClickListener {

    enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

    void onClick(DrawablePosition target);
}