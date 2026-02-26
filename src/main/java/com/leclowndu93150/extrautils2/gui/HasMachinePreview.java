package com.leclowndu93150.extrautils2.gui;

public interface HasMachinePreview {
    String getPreviewBaseTexture();
    String getPreviewFrontTexture();
    default int getPreviewTint() { return -1; }
}
