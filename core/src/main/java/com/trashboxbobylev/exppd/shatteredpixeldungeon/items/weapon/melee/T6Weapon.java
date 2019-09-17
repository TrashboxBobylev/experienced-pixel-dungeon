package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;

public class T6Weapon extends MeleeWeapon {
    {
        tier = 6;
    }

    public void gainExp( int exp ) {
        if (exp >= 500){
            while (exp >= 500){
                upgrade();
                exp -= 500;
            }
        }
    }
}
