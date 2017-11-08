package com.star.criminalintent;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class CrimeListFragment extends Fragment {

    public static final int REQUIRES_POLICE = 1;
    public static final int NOT_REQUIRES_POLICE = 0;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        updateUI();

        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mCrimeAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mCrimeAdapter);
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder {

        protected Crime mCrime;

        protected TextView mTitleTextView;
        protected CheckBox mSolvedCheckBox;
        protected TextView mDateTextView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup container, @LayoutRes int resource) {
            super(inflater.inflate(resource, container, false));

            itemView.setOnClickListener(v -> Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!", Toast.LENGTH_LONG).show());

            mTitleTextView = itemView.findViewById(R.id.list_item_crime_title_text_view);
            mSolvedCheckBox = itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mDateTextView = itemView.findViewById(R.id.list_item_crime_date_text_view);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
            mDateTextView.setText(mCrime.getFormattedDate());
        }
    }

    private class RegularCrimeHolder extends CrimeHolder {

        public RegularCrimeHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater, container, R.layout.list_item_crime);
        }
    }

    private class SeriousCrimeHolder extends CrimeHolder {

        private Button mRequiresPoliceButton;

        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater, container, R.layout.list_item_crime_requires_police);

            mRequiresPoliceButton = itemView.findViewById(R.id.list_item_crime_requires_police_button);

            mRequiresPoliceButton.setOnClickListener(v -> Toast.makeText(getActivity(),
                    mCrime.getTitle() + " requires police!", Toast.LENGTH_LONG).show());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            switch (viewType) {
                case REQUIRES_POLICE:
                    return new SeriousCrimeHolder(layoutInflater, parent);
                case NOT_REQUIRES_POLICE:
                    return new RegularCrimeHolder(layoutInflater, parent);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);

            return crime.isRequiresPolice() ? REQUIRES_POLICE : NOT_REQUIRES_POLICE;
        }
    }
}
