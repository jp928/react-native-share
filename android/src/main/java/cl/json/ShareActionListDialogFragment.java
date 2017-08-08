package cl.json;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Jingtai Piao
 */
public class ShareActionListDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ShareActionListDialogFragment";
    private static ArrayList<ResolveInfo> arrayListOfApps;
    private static final String SHAREABLE_APP_LIST = "shareable_app_list";
    private ShareActionSheetListener mListener;

    public static ArrayList<ResolveInfo> getArrayListOfApps() {
        return arrayListOfApps;
    }

    public static ShareActionListDialogFragment newInstance(List<ResolveInfo> shareableApps) {
        final ShareActionListDialogFragment fragment = new ShareActionListDialogFragment();
        final Bundle args = new Bundle();

        arrayListOfApps = new ArrayList<>(shareableApps.size());
        arrayListOfApps.addAll(shareableApps);

        args.putParcelableArrayList(SHAREABLE_APP_LIST, arrayListOfApps);
        fragment.setArguments(args);
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof ShareActionListDialogFragment) {
            ((ShareActionListDialogFragment) fragment).dismiss();
        }

        show(fragmentManager, TAG);
    }

    public void setActionSheetListener(ShareActionSheetListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shareaction_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShareActionAdapter(getArguments().getParcelableArrayList(SHAREABLE_APP_LIST)));

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        final ImageView icon;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_shareaction_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onButtonClicked(getArrayListOfApps(), getAdapterPosition());
                        dismiss();
                    }
                }
            });
        }

    }

    private class ShareActionAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final ArrayList<Parcelable> mShareableAppArrayList;

        ShareActionAdapter(ArrayList<Parcelable> shareableAppArrayList) {
            mShareableAppArrayList = shareableAppArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ResolveInfo resolverInfo = (ResolveInfo) mShareableAppArrayList.get(position);
            PackageManager pm = getContext().getPackageManager();
            holder.text.setText(resolverInfo.loadLabel(pm).toString());
            holder.icon.setImageDrawable(resolverInfo.loadIcon(pm));
        }

        @Override
        public int getItemCount() {
            return mShareableAppArrayList.size();
        }

    }

    public interface ShareActionSheetListener {
        void onDismiss(ShareActionListDialogFragment actionSheet, boolean isByBtn);

        void onButtonClicked( ArrayList<ResolveInfo> appList, int index);
    }

}
