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

package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.potions.brews;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Assets;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Freezing;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class FrigidBrew extends Brew {
	
	{
		image = ItemSpriteSheet.BREW_FRIGID;
	}
	
	@Override
	public void shatter(int cell) {
		
		if (Dungeon.level.heroFOV[cell]) {
			splash( cell );
			Sample.INSTANCE.play( Assets.SND_SHATTER );
		}
		
		for (int offset : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[cell+offset]) {
				GameScene.add(Blob.seed(cell + offset, 10, Freezing.class));
			}
		}
		GameScene.add( Blob.seed( cell, 1000, StormCloud.class ) );
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (30 + 60);
	}
	
	public static class Recipe extends com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfFrost.class, PotionOfStormClouds.class};
			inQuantity = new int[]{1, 1};
			
			cost = 2;
			
			output = FrigidBrew.class;
			outQuantity = 1;
		}
		
	}
}
