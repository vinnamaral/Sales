package au.com.vinnamaral.vendas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.vinnamaral.vendas.R;

public class MainActivity extends Activity implements Runnable {

	ProgressDialog pgd;
	Cursor cursor;
	SQLiteDatabase db;
	String error = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);

		StringBuilder sqlProdutos = new StringBuilder();
		sqlProdutos.append("CREATE TABLE IF NOT EXISTS [produtos](");
		sqlProdutos.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sqlProdutos.append("nome varchar(100), ");
		sqlProdutos.append("preco double(10,2));");
		db.execSQL(sqlProdutos.toString());

		db.execSQL("INSERT INTO produtos(nome, preco) VALUES('Cola Cola', '4.50')");
		db.execSQL("INSERT INTO produtos(nome, preco) VALUES('Red Bull', '6.50')");
		db.execSQL("INSERT INTO produtos(nome, preco) VALUES('TNT', '6.50')");
		db.execSQL("INSERT INTO produtos(nome, preco) VALUES('Red Label', '70.00')");

		StringBuilder sqlVendas = new StringBuilder();
		sqlVendas.append("CREATE TABLE IF NOT EXISTS [vendas](");
		sqlVendas.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sqlVendas.append("produto INTEGER, ");
		sqlVendas.append("preco double(10,2), ");
		sqlVendas.append("la double(10,9), ");
		sqlVendas.append("lo double(10,9)); ");
		db.execSQL(sqlVendas.toString());

		// db.execSQL("INSERT INTO vendas(produto, preco, la, lo) values('Coca Cola', '4.50', '-30.1087', '-51.3185')");
		// db.execSQL("INSERT INTO vendas(produto, preco, la, lo) values('TNT', '6.50', '-30.1087', '-51.3185')");

		db.close();
	}

	public void onResume() {
		super.onResume();

		TextView txvStatusConexao = (TextView) findViewById(R.id.txvStatusConexao);

		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conn.getNetworkInfo(0).isConnected()) {
			txvStatusConexao.setText("Status da conexão: 3G");
		} else if (conn.getNetworkInfo(1).isConnected()) {
			txvStatusConexao.setText("Status da conexão: Wifi");
		} else {
			((Button) findViewById(R.id.btnReplicar)).setEnabled(false);
			txvStatusConexao.setText("Status da conexão: desconectado");
		}
	}

	public void NovaVenda_Click(View v) {
		startActivity(new Intent(getBaseContext(), NovaVendaActivity.class));
	}

	public void ListarVendas_Click(View v) {
		startActivity(new Intent(getBaseContext(), ListarVendasActivity.class));
	}

	public void MapShow_Click(View v) {
		startActivity(new Intent(getBaseContext(), MapShowActivity.class));
	}

	public void Sair_Click(View v) {
		this.finish();
	}

	public void Replicacao_Click(View v) {
		db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);

		cursor = db.rawQuery("SELECT * FROM vendas", null);

		pgd = new ProgressDialog(MainActivity.this);
		pgd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pgd.setCancelable(false);
		pgd.setTitle("Replicando dados...");
		pgd.setMax(cursor.getCount());
		pgd.show();
		new Thread(MainActivity.this).start();
	}

	public void run() {
		int totalDB = cursor.getCount();
		int totalReplicado = 0;

		if (totalDB > 0) {
			while (cursor.moveToNext()) {
				StringBuilder strURL = new StringBuilder();
				strURL.append("http://192.168.1.5/vendas/inserir.php?produto=");
				strURL.append(cursor.getInt(cursor.getColumnIndex("produto")));
				strURL.append("&preco=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("preco")));
				strURL.append("&latitude=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("la")));
				strURL.append("&longitude=");
				strURL.append(cursor.getDouble(cursor.getColumnIndex("lo")));

				Log.d("MainActivity", strURL.toString());

				try {
					URL url = new URL(strURL.toString());
					HttpURLConnection http = (HttpURLConnection) url.openConnection();
					InputStreamReader ips = new InputStreamReader(http.getInputStream());
					BufferedReader line = new BufferedReader(ips);

					String linhaRetorno = line.readLine();

					if (linhaRetorno.equals("Y")) {
						db.delete("vendas", "_id=?", new String[] { String.valueOf(cursor.getInt(0)) });
						totalReplicado++;
						Log.d("ExportarVendasService", "OK");
						hl.sendEmptyMessage(0);
					}
				} catch (Exception ex) {
					error = ex.getMessage();
					hl.sendEmptyMessage(2);
				}
			}
			
			
			Intent it = new Intent("vendas.iniciar_servico");
			it.putExtra("totalDB", totalDB);
			it.putExtra("totalReplicado", totalReplicado);
			this.startService(it);

			if (totalDB == totalReplicado) {
				hl.sendEmptyMessage(1);
			} else {
				error = "Ocorreu algum erro no sistema.";
				hl.sendEmptyMessage(2);
			}
		} else {
			hl.sendEmptyMessage(3);
		}
	}

	public Handler hl = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				pgd.setProgress(pgd.getProgress() + 1);
			} else if (msg.what == 1) {
				pgd.dismiss();
				Toast.makeText(MainActivity.this, "Sucesso na replicação!", Toast.LENGTH_LONG).show();
			} else if (msg.what == 2) {
				pgd.dismiss();
				Toast.makeText(MainActivity.this, "Erro na replicação: " + error, Toast.LENGTH_LONG).show();
			} else if (msg.what == 3) {
				pgd.dismiss();
				Toast.makeText(MainActivity.this, "Não há dados para replicar!", Toast.LENGTH_LONG).show();
			}
		}
	};
}
