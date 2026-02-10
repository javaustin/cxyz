package com.carrotguy69.cxyz.other.utils;

public enum NotePitch {// Notes
    LOW_F_SHARP(0.5000F),
    LOW_G(0.5300F),
    LOW_G_SHARP(0.5610F),
    LOW_A(0.5950F),
    LOW_A_SHARP(0.6300F),
    LOW_B(0.6670F),
    LOW_C(0.7070F),
    LOW_C_SHARP(0.7490F),
    LOW_D(0.7940F),
    LOW_D_SHARP(0.8410F),
    LOW_E(0.8910F),
    LOW_F(0.9440F),
    F_SHARP(1.000F),
    G(1.059F),
    G_SHARP(1.122F),
    A(1.189F),
    A_SHARP(1.260F),
    B(1.335F),
    C(1.414F),
    C_SHARP(1.498F),
    D(1.587F),
    D_SHARP(1.682F),
    E(1.782F),
    F(1.888F),
    HIGH_F_SHARP(2F);

    public final float pitch;

    NotePitch(float pitch) {
        this.pitch = pitch;
    }
}
