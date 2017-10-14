package todo.domain.repository.todo;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

public class DbsetupOperations {
	
	/*
	 * テーブルデータを初期化するメソッド
	 */
	public static final Operation INIT_TABLE =
			Operations.sequenceOf(
					Operations.deleteAllFrom("todo"));
	
	/*
	 * テーブルにデータをセットアップするメソッド（パターンＡ）
	 */
	public static final Operation SETUP_TABLE_A =
			Operations.sequenceOf(
					Operations.insertInto("todo")
						.columns("todo_id","todo_title","finished","created_at")
						.values("cceae402-c5b1-440f-bae2-7bee19dc17fb","one",false,"2017-10-01 15:39:17.888")
						.values("5dd4ba78-ff5b-423b-aa2a-a07118aeaf90","two",false,"2017-10-01 15:39:19.981")
						.values("e3bdb9af-3dde-40b7-b5fb-4b388567ab45","three",false,"2017-10-01 15:39:28.437")
						.build()
			);
	
	/*
	 * テーブルにデータをセットアップするメソッド（パターンＢ）
	 * パターンＡとの違いは1行ずつカラムに対してデータを指定している点。
	 */
	public static final Operation SETUP_TABLE_B =
			Operations.sequenceOf(
					Operations.insertInto("todo")
					//row0
					.row()
						.column("todo_id", "cceae402-c5b1-440f-bae2-7bee19dc17fb")
						.column("todo_title", "one")
						.column("finished", false)
						.column("created_at", "2017-10-01 15:39:17.888")
						.end()
					//row1
					.row()
						.column("todo_id", "5dd4ba78-ff5b-423b-aa2a-a07118aeaf90")
						.column("todo_title", "two")
						.column("finished", false)
						.column("created_at", "2017-10-01 15:39:19.981")
						.end()
					//row2
					.row()
						.column("todo_id", "e3bdb9af-3dde-40b7-b5fb-4b388567ab45")
						.column("todo_title", "three")
						.column("finished", false)
						.column("created_at", "2017-10-01 15:39:28.437")
						.end()
					.build()
			);
	
}
