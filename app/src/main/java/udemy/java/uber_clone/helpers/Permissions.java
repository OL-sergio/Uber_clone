package udemy.java.uber_clone.helpers;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class Permissions {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> permissionsToRequest  = new ArrayList<>();

            /*Percorre as permissões passadas,
            verificando uma a uma
            * se já tem a permissao liberada */
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }

            /*Caso a lista esteja vazia, não é necessário solicitar permissão*/
            if ( !permissionsToRequest .isEmpty() ) {

                String[] permissionsArray  = new String[ permissionsToRequest .size() ];
                permissionsToRequest .toArray( permissionsArray  );

                //Solicita permissão
                ActivityCompat.requestPermissions(activity, permissionsArray , requestCode );
            }
        }

        return true;

    }

}
