package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.armor;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class EchArmor extends Armor {
    {
        image = ItemSpriteSheet.ARMOR_HOLDER;

        bones = false; //Finding them in bones would be semi-frequent and disappointing.
    }

    public EchArmor() {
        super( 6 );
    }

}
