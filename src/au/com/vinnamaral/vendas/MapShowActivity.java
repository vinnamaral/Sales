package au.com.vinnamaral.vendas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import au.com.vinnamaral.vendas.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapShowActivity extends FragmentActivity {

	// private LatLng location = new LatLng(0, 0);

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		Intent it = getIntent();
		LatLng location = new LatLng(it.getDoubleExtra("latitude", 0), it.getDoubleExtra("longitude", 0));
		// location.latitude = it.getDoubleExtra("latitude", 0);
		// location.longitude = it.getDoubleExtra("longitude", 0);

//		MarkerOptions marcador = new MarkerOptions();
//		marcador.position(location);
//		marcador.title("Vinicius Amaral");
//		map.addMarker(marcador);
		map.addMarker(new MarkerOptions().position(location).title("Vinicius Amaral"));

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));

		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

	}

}
