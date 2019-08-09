package ior.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.engine.Company;
import ior.engine.ServerHandler;

public class GridAdapter extends BaseAdapter {

    private List<Company> companies;
    private Context context;
    private int mViewResourceId;

    public GridAdapter(Context context, int viewResourceId, List<Company> companies) {

        this.context = context;
        this.companies = companies;
        this.mViewResourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int position) {
        return companies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null) {


            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(mViewResourceId, parent, false);
        }

        Company company = companies.get(position);
        ImageView imageView = convertView.findViewById(R.id.imageView_companiesGridAdapter);
        TextView textView = convertView.findViewById(R.id.textView_companiesGridAdapter);
        LinearLayout linearLayout = convertView.findViewById(R.id.linearLayouy_companiesGridAdapter);

        linearLayout.setTag(company.getName());
        Bitmap bitmap = company.getBitmap();
        textView.setText(company.getName());






        //ImageView imageView = new ImageView(context);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundResource(R.drawable.rounded_image);
        //imageView.setTag(companies.get(position).getName());
        //imageView.setLayoutParams(new ViewGroup.LayoutParams(190, 190));
        //Bitmap bitmap = companies.get(position).getBitmap();
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
        imageView.setImageBitmap(circleBitmap);

        return convertView;
    }
}
