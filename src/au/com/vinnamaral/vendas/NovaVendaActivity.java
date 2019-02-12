package au.com.vinnamaral.vendas;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import au.com.vinnamaral.vendas.R;

public class NovaVendaActivity extends Activity implements LocationListener {

	LocationManager locationManager = null;
	ProgressDialog pgd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nova_venda_activity);

		Spinner spProdutos = (Spinner) findViewById(R.id.spProdutos);

		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT * FROM produtos ORDER BY nome ASC", null);

		String[] from = { "_id", "nome", "preco" };
		int[] to = { R.id.txvId, R.id.txvNome, R.id.txvPreco };

		SimpleCursorAdapter ad = new SimpleCursorAdapter(getBaseContext(), R.layout.spinner_model, c, from, to);

		spProdutos.setAdapter(ad);

		db.close();
	}
	
	public void Salvar_Click(View view) {

		// Retorna o gerenciamento do servi�o de localiza��o do celular
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Provedor de acesso a localiza��o do GPS
		String provider = "gps";

		// A cada 4s o m�todo onLocationChanged ser� chamado
		locationManager.requestLocationUpdates(provider, 4000, 0, this);
		pgd = ProgressDialog.show(NovaVendaActivity.this, "Aguarde...", "Buscando localiza��o!", true, false, null);

	}

	@Override
	public void onLocationChanged(Location location) {
		// Se entrou nessa fun��o, � porque conseguiu recuperar minha localiza��o
		// Sendo assim, eu paro a barra de progresso
		pgd.dismiss();

		// Cancela o listener
		// O listener sou eu mesmo (this)
		locationManager.removeUpdates(this);

		// Salva as informa��es
		SQLiteDatabase db = openOrCreateDatabase("vendas.db", Context.MODE_PRIVATE, null);

		Spinner spProdutos = (Spinner) findViewById(R.id.spProdutos);
		// Retorna o item que est� selecionado no Spinner. Para isso usamos adaptadores do cursor
		SQLiteCursor dados = (SQLiteCursor) spProdutos.getAdapter().getItem(spProdutos.getSelectedItemPosition());

		// ContentValues para passar os valores que eu quero salvar no banco de dados
		ContentValues ctv = new ContentValues();
		ctv.put("produto", dados.getInt(0)); // O _id esta na posi��o 0 do meu array from
		ctv.put("preco", dados.getInt(2));
		ctv.put("la", location.getLatitude());
		ctv.put("lo", location.getLongitude());

		if (db.insert("vendas", "_id", ctv) > 0) {
			Toast.makeText(getBaseContext(), "Salvo com sucesso", Toast.LENGTH_LONG).show();
		}

		db.close();
		NovaVendaActivity.this.finish();

	}

	@Override
	public void onProviderDisabled(String provider) {
		pgd.dismiss();

		Toast.makeText(getBaseContext(), "� necess�rio ativar o GPS!", Toast.LENGTH_LONG).show();

		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

}
