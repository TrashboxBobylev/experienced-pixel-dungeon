/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Jjango extends T6Weapon {

	{
		image = ItemSpriteSheet.JJANGO;

		ACC = 1.20f; //20% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  50 +    //50 base
				9*lvl;   //+6
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
	    Buff.affect(defender, Bleeding.class).set(damage+level()*10);

        return super.proc(attacker, defender, damage);
    }

    @Override
    public float accuracyFactor(Char owner) {
        return super.accuracyFactor(owner)*(1f+0.5f*level());
    }
}
