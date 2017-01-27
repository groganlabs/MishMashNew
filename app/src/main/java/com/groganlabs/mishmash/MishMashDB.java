package com.groganlabs.mishmash;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class MishMashDB extends SQLiteOpenHelper {
	public static final int latestVersion = 1;
	
	public static final int JUMBLE_MASK = 1;
	public static final int CRYPTO_MASK = 2;
	public static final int DROP_MASK = 4;
	
	public static final String DB_NAME = "mishMashDb";
	
	private static final String GAME_TABLE = "phrase";
	private static final String GAME_TABLE_ID = "_id";
	private static final String COL_COMPLETED = "completed";
	private static final String COL_ELIGIBLE = "eligible_for";
	private static final String COL_GAME = "phrase";
	private static final String COL_PACK = "pack";
	
	private static final String PACK_TABLE = "game_pack";
	private static final String PACK_ID	= "_id";
	private static final String PACK_NAME = "name";
	private static final String PACK_DESC = "description";
	private static final String PACK_PURCHASED = "purchased";
	private static final String PACK_NUMGAMES = "num_games";
	private static final String PACK_SKU = "sku";
	
	private static final String ACTIVE_GAME_TABLE = "active_game";
	private static final String ACTIVE_GAME_ID = "_id";
	private static final String ACTIVE_GAME_GAME_ID = "game_id";
	private static final String ACTIVE_GAME_TYPE = "game_mask";
	private static final String ACTIVE_GAME_ANSWER = "user_answer";
	private static final String ACTIVE_GAME_SOLUTION = "solution";
	private static final String ACTIVE_GAME_PUZZLE = "puzzle";
	private static final String ACTIVE_GAME_DATE = "saved";
	
	private static final String GAME_TABLE_CREATE = "create table " + GAME_TABLE +
			"(" + GAME_TABLE_ID + " integer primary key, " +
			COL_PACK + " integer, " +
			COL_ELIGIBLE + " integer default 7, " + 
			COL_COMPLETED + " integer default 0, " +
			COL_GAME + " varchar);";
	
	private static final String PACK_TABLE_CREATE = "create table " + PACK_TABLE +
			"(" + PACK_ID + " integer primary key, " +
			PACK_NAME + " varchar, " +
			PACK_DESC + " varchar, " +
			PACK_NUMGAMES + " integer, " +
			PACK_SKU + " integer, " +
			PACK_PURCHASED + " integer);";
	
	private static final String ACTIVE_TABLE_CREATE = "create table " + ACTIVE_GAME_TABLE +
			"(" + ACTIVE_GAME_ID + " integer primary key, " +
			ACTIVE_GAME_GAME_ID + " integer, " +
			ACTIVE_GAME_TYPE + " integer, " +
			ACTIVE_GAME_ANSWER + " varchar, " +
			ACTIVE_GAME_SOLUTION + " varchar, " +
			ACTIVE_GAME_DATE + " date, " +
			ACTIVE_GAME_PUZZLE + " varchar);";

	private Context mContext;

	public MishMashDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("create table phrase(_id integer primary key, phrase varchar, pack integer, completed integer default 0, eligible_for integer default 7);");
		//         "create table phrase(_id integer primary key, phrase varchar, pack integer, completed integer default 0, eligible_for integer default 7);"
		db.execSQL(GAME_TABLE_CREATE);
		db.execSQL(PACK_TABLE_CREATE);
		db.execSQL(ACTIVE_TABLE_CREATE);

		String[] packNames = mContext.getResources().getStringArray(R.array.availablePackNames);
		String[] packDesc = mContext.getResources().getStringArray(R.array.availablePackDescs);
		String[] games = mContext.getResources().getStringArray(R.array.defaultSolutions);
		ContentValues values = new ContentValues();
		ContentValues packVals = new ContentValues();

		packVals.put(PACK_NAME, packNames[0]);
		packVals.put(PACK_DESC, packDesc[0]);
		packVals.put(PACK_NUMGAMES, 4);
		packVals.put(PACK_PURCHASED, 1);
		db.insert(PACK_TABLE, null, packVals);

		packVals.put(PACK_PURCHASED, 0);
		for(int ii = 1; ii < packNames.length; ii++) {
			Log.d("db", "inserting game " + packNames[ii]);
			packVals.put(PACK_NAME, packNames[ii]);
			packVals.put(PACK_DESC, packDesc[ii]);
			db.insert(PACK_TABLE, null, packVals);
		}

		packVals.put(PACK_PURCHASED, 0);
		values.put(COL_PACK, 1);
		for(int ii = 0; ii < games.length; ii++) {
			values.put(COL_GAME, games[ii]);
			db.insert(GAME_TABLE, null, values);
		}

		String games2[] = mContext.getResources().getStringArray(R.array.packTwo);
		values.put(COL_PACK, 2);
		for(int ii = 0; ii < games2.length; ii++) {
			values.put(COL_GAME, games2[ii]);
			db.insert(GAME_TABLE, null, values);
		}

		String games3[] = mContext.getResources().getStringArray(R.array.packThree);
		values.put(COL_PACK, 3);
		for(int ii = 0; ii < games3.length; ii++) {
			values.put(COL_GAME, games3[ii]);
			db.insert(GAME_TABLE, null, values);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


	}

	/**
	 * gets a game, any game. Uses the app settings to determine
	 * game reuse options
	 * @param game Game object we're getting the game for
	 * @return true if a game was found, false if no results
	 */
	public boolean getGame(Game game) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		//TODO: use the game's properties to determine whether we need a random game or pack
		// also, need to figure out which packs to use
		SQLiteDatabase db = getWritableDatabase();
		//if isset game.gameId && gameId > 0
		if(game.gameId > 0) {
			// see if it's in the active table, if so get it from there otherwise get it as usual
			String select = "SELECT * from " + ACTIVE_GAME_TABLE + " where " + ACTIVE_GAME_GAME_ID + " = ? AND " + ACTIVE_GAME_TYPE + " = ?";
			String[] params = {String.valueOf(game.gameId), String.valueOf(game.gameType)};
			Cursor res = db.rawQuery(select, params);
			if(res.getCount() > 0) {
				res.moveToLast();
			}
			return true;
		}

		Log.d("db", "starting to build the query");
		String select = "SELECT " + COL_GAME + ", " + GAME_TABLE + "." +GAME_TABLE_ID + " FROM " + GAME_TABLE + ", " + PACK_TABLE + " ";
		StringBuilder where = new StringBuilder();
		where.append("WHERE (" + COL_ELIGIBLE + " & " + game.getGameType() + ") = " + game.getGameType());
		Log.d("db", where.toString());
		where.append(" AND " + GAME_TABLE + "." + GAME_TABLE_ID + " NOT IN (SELECT " + ACTIVE_GAME_GAME_ID + " FROM " +
				ACTIVE_GAME_TABLE + ")");
		where.append(" AND " + COL_PACK + " = " + PACK_TABLE + "." + PACK_ID + " AND " + PACK_PURCHASED + " = 1 ");
		Log.d("db", where.toString());
		//if we aren't reusing or replaying games, add COL_COMPLETED  = 0
		if(!sharedPref.getBoolean("PREF_REUSE", true) && !sharedPref.getBoolean("PREF_REPLAY", false))
			where.append(" AND " + COL_COMPLETED + " = 0");
		//else if we are reusing but not replaying games,
		else if(sharedPref.getBoolean("PREF_REUSE", true) && !sharedPref.getBoolean("PREF_REPLAY", false))
			where.append(" AND (" + COL_COMPLETED + " & " + game.getGameType() + ") != " + game.getGameType());
		//if we're reusing and replaying, it doesn't matter 
		Log.d("db", where.toString());
		Log.d("db", "Where: "+where.toString());
		Cursor res = db.rawQuery(select + where.toString(), null);
		if(res.getCount() == 0)
			return false;
		
		Log.d("db", "res.getCount() = " + res.getCount());
		Random rand = new Random();
		res.moveToPosition(rand.nextInt(res.getCount()));
		
		game.setSolution(res.getString(0));
		game.setGameId(res.getInt(1));
		
		return true;
	}
	
	public void removeActiveGame(Game game) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(ACTIVE_GAME_TABLE, ACTIVE_GAME_GAME_ID + " = " + game.getGameId(), null);
	}

	public void markGameWon(Game game) {
		SQLiteDatabase db = getWritableDatabase();
		String query = "update " + GAME_TABLE + " set " + COL_COMPLETED + " = " + 
				"(" + COL_COMPLETED + " | " + game.getGameType() + ") " + 
				" where " + GAME_TABLE_ID + " = " + game.getGameId();
		Log.d("db", "SQL: " + query);
		db.execSQL(query);
		
		//for debugging
		/*String[] columns = {GAME_TABLE_ID, COL_GAME, COL_COMPLETED};
		Cursor res = db.query(GAME_TABLE, columns, where, null, null, null, null);
		if(res.getCount() > 0) {
			res.moveToFirst();
			Log.d("db", "gameId: " + res.getString(0));
			Log.d("db", "completed: " + res.getString(2));
		}*/
		
	}

	public void saveGame(Game game) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String where = ACTIVE_GAME_GAME_ID + " = " + game.gameId + " AND " + ACTIVE_GAME_TYPE + " = " + game.gameType;

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ACTIVE_GAME_GAME_ID, game.gameId);
		values.put(ACTIVE_GAME_PUZZLE, String.valueOf(game.getPuzzleArr()));
		values.put(ACTIVE_GAME_ANSWER, String.valueOf(game.getAnswerArr()));
		values.put(ACTIVE_GAME_SOLUTION, game.getSolution());
		values.put(ACTIVE_GAME_DATE, df.format(date));
		values.put(ACTIVE_GAME_TYPE, game.gameType);

		// try updating where gameId and gameType
		// if 0 returned, insert
		if(db.update(ACTIVE_GAME_TABLE, values, where, null) == 0) {
			db.insert(ACTIVE_GAME_TABLE, null, values);
		}
		db.close();
	}

	public Cursor getSavedGames(Game game) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "select " + ACTIVE_GAME_ID + ", " + ACTIVE_GAME_GAME_ID + ", " + ACTIVE_GAME_ANSWER + ", " +
				ACTIVE_GAME_PUZZLE + ", " + ACTIVE_GAME_SOLUTION + " from " + ACTIVE_GAME_TABLE;
		Cursor cur = db.rawQuery(query, null);
		return cur;
	}

	public Cursor getPacks() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur;
		String query = "SELECT " + PACK_NAME + ", " + PACK_DESC + ", " + PACK_NUMGAMES + ", " + PACK_PURCHASED  + ", " + PACK_ID + ", " + PACK_SKU + " FROM " + PACK_TABLE;
		cur = db.rawQuery(query, null);
		return cur;
	}

	/**
	 * Sets all packs' purchased column to 0
	 * Should only be run after successfully querying
	 * Google Play.
	 */
	public void clearOwnedPacks() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(PACK_PURCHASED, 0);
		db.update(PACK_TABLE, cv, null, null);
	}

}
