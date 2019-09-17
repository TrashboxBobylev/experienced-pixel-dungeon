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

package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.scrolls.exotic;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Assets;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.DepthyMob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Mimic;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Flare;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Speck;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Generator;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Item;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.wands.CursedWand;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfPolymorph extends ExoticScroll {
	
	{
		initials = 10;
	}
	
	@Override
	public void doRead() {
		
		new Flare( 5, 32 ).color( 0xFFFFFF, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();

        setKnown();

        readAnimation();

		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (true) {
				if (!mob.properties().contains(Char.Property.BOSS)
						&& !mob.properties().contains(Char.Property.MINIBOSS) || (mob instanceof DepthyMob && Random.Float() < 0.1f)){
					/*Sheep sheep = new Sheep();
					sheep.lifespan = 10;
					sheep.pos = mob.pos;

					//awards half exp for each sheep-ified mob
					//50% chance to round up, 50% to round down
					if (mob.EXP % 2 == 1) mob.EXP += Random.Int(2);
					mob.EXP /= 2;

					mob.destroy();
					mob.sprite.killAndErase();
					Dungeon.level.mobs.remove(mob);
					TargetHealthIndicator.instance.target(null);
					GameScene.add(sheep);
					CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);*/
                    Mimic mimic = Mimic.spawnAt(mob.pos, new ArrayList<Item>());
                    if (mimic != null) {
                        mimic.adjustStats(Dungeon.depth + 80);
                        mimic.HP = mimic.HT;
                        Sample.INSTANCE.play(Assets.SND_MIMIC, 1, 1, 0.5f);
                        ArrayList<Item> reward = RingOfWealth.tryForBonusDrop(mob, 20);
                        if (reward != null) mimic.items = reward;
                        mob.destroy();
                        mob.sprite.killAndErase();
                        Dungeon.level.mobs.remove(mob);
                        TargetHealthIndicator.instance.target(null);
                        GameScene.add(mimic);
                    } else {
                        GLog.i(Messages.get(CursedWand.class, "nothing"));
                    }
				}
			}
		}
		
	}
	
}
