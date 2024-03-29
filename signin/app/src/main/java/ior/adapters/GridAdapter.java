package ior.adapters;

import android.content.Context;
import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.activities.CompanyReceiptsActivity;
import ior.engine.Company;
import ior.engine.ServerHandler;

public class GridAdapter extends BaseAdapter {

    private List<Company> companies;
    private Context context;
    private int mViewResourceId;
    private String userEmail;

    public GridAdapter(Context context, int viewResourceId, List<Company> companies, String email) {

        this.context = context;
        this.companies = companies;
        this.mViewResourceId = viewResourceId;
        this.userEmail = email;
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
        Bitmap bitmap = company.getBitmap() != null ? company.getBitmap()
                :
                BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image_logo);
        textView.setText(company.getName());



//        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        Paint paint = new Paint();
//        paint.setShader(shader);
//        paint.setAntiAlias(true);
//        Canvas c = new Canvas(circleBitmap);
//        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
//        imageView.setImageBitmap(circleBitmap);
        imageView.setImageBitmap(bitmap);


        linearLayout.setOnClickListener(v -> {

            Intent intent = new Intent(context, CompanyReceiptsActivity.class);
            intent.putExtra("email", userEmail);
            intent.putExtra("company",company.getName());
            intent.putExtra("barTitle", company.getName() + "'s Receipts");
            context.startActivity(intent);


//            ServerHandler.getInstance().fetchCompanyReceipts(userEmail, company.getName(), () -> {
//
//
//                Intent intent = new Intent(context, CompanyReceiptsActivity.class);
//                intent.putExtra("email", userEmail);
//                intent.putExtra("company",company.getName());
//                context.startActivity(intent);
//            });
        });

        //textView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.companies_logos_transmition));
        linearLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.companies_logos_fade));

        return convertView;
    }
}
