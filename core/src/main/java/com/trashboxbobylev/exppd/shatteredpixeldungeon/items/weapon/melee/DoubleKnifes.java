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
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DoubleKnifes extends T6Weapon {

	{
		image = ItemSpriteSheet.TWIN_BLADES;
	}

	@Override
	public int max(int lvl) {
		return 28 +    //28
                        lvl*8;   //+8 per level
	}

	@Override
	public int defenseFactor( Char owner ) {
		return 25+15*level();     //25 extra defence, plus 15 per level;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 25+15*level());
		} else {
			return Messages.get(this, "typical_stats_desc", 5);
		}
	}
}