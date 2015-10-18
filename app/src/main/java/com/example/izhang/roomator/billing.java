package com.example.izhang.roomator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class billing extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String account_id = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Global variables for fragment view
    ListView billList;
    View view;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment billing.
     */
    // TODO: Rename and change types and number of parameters
    public static billing newInstance(String param1, String param2) {
        billing fragment = new billing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public billing() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account_id = getArguments().getString("account_id");
        }
    }

    // TODO: 10/10/15 : Complete the click of the FAB button by adding that data into the firebase after changes 
    // TODO: 10/10/15 : Add all the bills that this particular person needs to pay into the listview 
    // TODO: 10/13/15 : When the user clicks on listview item, confirm payment, and pay using venmo
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_billing, container, false);
        billList = (ListView) view.findViewById(R.id.billList);

        //Setup Firebase
        Firebase.setAndroidContext(getActivity());
        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create list of bills for user

                final String groupID = dataSnapshot.child("account").child(account_id).child("group").getValue().toString();
                final ArrayList<bills> billings = new ArrayList<bills>();

                int choresCount = 1;
                final Iterable<DataSnapshot> billIter = dataSnapshot.child("group").child(groupID).child("bills").getChildren();
                for (DataSnapshot d : billIter) {
                    String ownerID = d.child("owner").getValue().toString();
                    String cost = d.child("amount").getValue().toString();
                    String description = d.child("description").getValue().toString();
                    bills temp = new bills(10, description, 2);
                    billings.add(temp);
                    choresCount++;
                }

                final ArrayAdapter adapter = new ArrayAdapter<bills>(getActivity(),
                        android.R.layout.simple_list_item_1, billings);
                billList.setAdapter(adapter);

                billList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder payBuilder = new AlertDialog.Builder(getActivity());
                        payBuilder.setTitle("Confirm to pay?");
                        payBuilder.setMessage(billings.get(position).getDesc());

                        payBuilder.setPositiveButton("Confirm Payment", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "You have just paid for this!", Toast.LENGTH_LONG);
                            }
                        });
                        payBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        payBuilder.show();

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.material_blue_grey_800));

        // Prompts user for input to record the cost per person and description
        fab.setOnClickListener(new View.OnClickListener() {
            String description;
            String cost;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add New Bill");

                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input for description
                final EditText descriptionInput = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                descriptionInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                descriptionInput.setHint("Cable Internet");
                layout.addView(descriptionInput);

                // Set up the input for per person billing
                final EditText costInput = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                costInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                costInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                costInput.setHint("15.00");
                layout.addView(costInput);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        description = descriptionInput.getText().toString();
                        cost = costInput.getText().toString();

                        Toast.makeText(getActivity(), "Description: " + description + "  CostPerPerson: " + cost, Toast.LENGTH_LONG).show();


                        //adapter.setNotifyOnChange(true);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
