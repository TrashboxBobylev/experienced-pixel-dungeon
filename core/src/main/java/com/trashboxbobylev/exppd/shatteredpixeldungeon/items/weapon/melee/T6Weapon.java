package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class T6Weapon extends MeleeWeapon {
    {
        tier = 6;
    }

    public int exp;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("exp", exp);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        exp = bundle.getInt("exp");
    }

    public void gainExp(int exp ) {
        this.exp += exp;
        if (this.exp >= 500){
            while (this.exp >= 500){
                upgrade();
                this.exp -= 500;
            }
        }
    }

    @Override
    public String info() {
        String inf = super.info();
        inf += " " + Messages.get(T6Weapon.class, "exp", 500 - exp);
        return inf;
    }
}
