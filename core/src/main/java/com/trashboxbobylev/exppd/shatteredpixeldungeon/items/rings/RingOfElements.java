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

package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.rings;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Electricity;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Charm;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Chill;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Frost;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Ooze;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Poison;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Weakness;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Eye;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Shaman;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Warlock;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Yog;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;

import java.text.DecimalFormat;
import java.util.HashSet;

public class RingOfElements extends Ring {
	
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.84f, soloBonus()))));
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(16f));
		}
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Resistance();
	}
	
	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( Burning.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Chill.class );
		RESISTS.add( Frost.class );
		RESISTS.add( Ooze.class );
		RESISTS.add( Paralysis.class );
		RESISTS.add( Poison.class );
		RESISTS.add( Corrosion.class );
		RESISTS.add( Weakness.class );
		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );
		
		RESISTS.add( ToxicGas.class );
		RESISTS.add( Electricity.class );
		
		//FIXME currently this affects all attacks, not just longranged magic
		RESISTS.add( Shaman.class );
		RESISTS.add( Warlock.class );
		RESISTS.add( Eye.class );
		RESISTS.add( Yog.BurningFist.class );
	}
	
	public static float resist( Char target, Class effect ){
		if (getBonus(target, Resistance.class) == 0) return 1f;
		
		for (Class c : RESISTS){
			if (c.isAssignableFrom(effect)){
				return (float)Math.pow(0.84, getBonus(target, Resistance.class));
			}
		}
		
		return 1f;
	}
	
	public class Resistance extends RingBuff {
	
	}
}
