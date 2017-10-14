package todo.domain.repository.todo;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;

import todo.domain.model.Todo;

/**
 * Repository Test
 * DBsetupによるデータのセットアップ、比較
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
@Transactional
public class TodoRepositoryTestVerDBsetup {

	@Inject
	TodoRepository target;
	
	@Inject
	DataSource dataSource;
	
	@Inject
	JdbcTemplate jdbctemplate;
	
	private static DbSetupTracker TRACKER = new DbSetupTracker();
	
	@Before
	public void setUp() {
		//DBSetupを使う準備
		Destination dest = new DataSourceDestination(dataSource);
		//sequenceOfメソッドで囲むことによって、連続してデータに対しての処理を実行できる。
		Operation ops = Operations.sequenceOf(DbsetupOperations.INIT_TABLE,
												DbsetupOperations.SETUP_TABLE_A);
		DbSetup dbSetup = new DbSetup(dest, ops);
		
		//TRACKERでセットアップを制御する。
		TRACKER.launchIfNecessary(dbSetup);
	}
	
	
	@Test
	public void testUpdate() throws Exception{
		//テスト用のデータを作成（getTodoDataメソッドはDBからデータを取得するprivateメソッド。取得したデータを書き換えて更新する。）
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String todoId = "cceae402-c5b1-440f-bae2-7bee19dc17fb";
		Todo testDataTodo = getTodoData(todoId);
		testDataTodo.setFinished(true);
		
		//updateメソッドのテスト
		boolean actTodo = target.update(testDataTodo);
		
		//結果検証
		assertEquals(actTodo, true);
		
		//期待値の作成
		Todo exptodo = new Todo();
		exptodo.setTodoId("cceae402-c5b1-440f-bae2-7bee19dc17fb");
		exptodo.setTodoTitle("one");
		exptodo.setFinished(true);
		String strDate = "2017-10-01 15:39:17.888";
		Date date = sdFormat.parse(strDate);
		exptodo.setCreatedAt(date);
		
		//処理後データの取得（getTodoDataメソッドはDBからテスト後に変更されたデータを取得するprivateメソッド）
		Todo actTestDataTodo = getTodoData(todoId);
		
		//メソッド実行後テーブルデータ検証
		//date型の表示形式が異なるため、時刻文字列に変換して比較している
		assertEquals(exptodo.getTodoId(), actTestDataTodo.getTodoId());
		assertEquals(exptodo.getTodoTitle(), actTestDataTodo.getTodoTitle());
		assertEquals(exptodo.isFinished(), actTestDataTodo.isFinished());
		assertEquals(sdFormat.format(exptodo.getCreatedAt()) ,sdFormat.format(actTestDataTodo.getCreatedAt()));
		
		/*
		 * TRACKERによるセットアップの制御
		 * テスト対象が参照系（select）の場合・・・参照のみでありデータベースを更新していないので、データの再セットアップは不要。
		 * テスト対象が更新系(insert, update, delete）の場合・・・更新されたデータベースのままだと次のテストに影響を与えるので、データのセットアップをし直す必要がある。
		 * TRACKER.skipNextLaunchメソッドを実行することで、次のテスト（@Test）の前に実行される@Beforeのデータセットアップ処理をスキップすることができる。
		 * 
		 * 今回はupdateメソッドがテスト対象のため、TRACKER.skipNextLaunch();をコメントアウトしている。
		 */
		//TRACKER.skipNextLaunch();
		
	}
	
	//テスト用元データの取得
	private Todo getTodoData(String todoId) {
		
		String sql = "SELECT * FROM todo WHERE todo_id=?";
		
		Todo todoData = (Todo)jdbctemplate.queryForObject(sql, new Object[] {todoId},
				new RowMapper<Todo>() {
					public Todo mapRow(ResultSet rs, int rownum) throws SQLException {
						Todo todoSql = new Todo();
						
						todoSql.setTodoId(rs.getString("todo_id"));
						todoSql.setTodoTitle(rs.getString("todo_title"));
						todoSql.setFinished(rs.getBoolean("finished"));
						todoSql.setCreatedAt(rs.getTimestamp("created_at"));
					
						return todoSql;
					}
		});
		return todoData;
	}

}
