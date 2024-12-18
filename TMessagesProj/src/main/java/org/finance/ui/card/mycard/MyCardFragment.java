package org.finance.ui.card.mycard;

import static com.google.android.material.tabs.TabLayout.*;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.room.CardsDao;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.GetCardService;
import org.finance.helpers.RemoveView;
import org.finance.ui.monitoring.MonitoringFragment;
import org.finance.ui.p2p.local.CardInfoFragment;
import org.finance.ui.paynet.category.PaynetCategoryFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** @noinspection ALL*/
public class MyCardFragment extends BaseFragment {
    private  GetCardBalanceWithCards response;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardAdapter cardAdapter;
    private CardsDao cardDao;
    private SuperAppDatabase database;
    TLRPC.User user;
    private   BottomSheetDialog bottomSheetDialog;
    String cardBlockectext;
    String cardBlockectDialod;
    int lockId;
    private GetCardService getCardHelper;
   private TabLayout tabLayout;



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View createView(@NonNull Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setId(View.generateViewId());
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setId(View.generateViewId());
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        cardDao = database.cardsDao();
        getCardHelper=new GetCardService(context);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.my_cards));
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    RemoveView.clearsView(layout);
                    finishFragment();
                    clearViews();
                }
            }
        });


         tabLayout = new TabLayout(context);
        tabLayout.setId(View.generateViewId());
        tabLayout.setTabMode(MODE_SCROLLABLE);
        tabLayout.setBackgroundColor(Color.parseColor("#F5F5F5")); // TabLayout orqa foni
        Tab tabAll = tabLayout.newTab().setText(LocaleController.getString(R.string.all));
        Tab tabInSum = tabLayout.newTab().setText(LocaleController.getString(R.string.in_sums));
        Tab tabInternational = tabLayout.newTab().setText(LocaleController.getString(R.string.international));
        tabLayout.addTab(tabAll);
        tabLayout.addTab(tabInSum);
        tabLayout.addTab(tabInternational);
        tabLayout.setTabTextColors(Color.GRAY, Color.BLACK);
        tabLayout.setSelectedTabIndicator(null);
        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    tabView.setBackgroundResource(R.drawable.tab_selected_background);
                    ((TextView) tabView).setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    tabView.setBackgroundColor(Color.TRANSPARENT);
                    ((TextView) tabView).setTextColor(Color.GRAY);
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(context);
                tabTextView.setText(tab.getText());
                tabTextView.setTextSize(16);
                Typeface typeface = Typeface.create("SF Pro Text", Typeface.BOLD);
                tabTextView.setTypeface(typeface, Typeface.BOLD);
                tabTextView.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), AndroidUtilities.dp(8));
                tabTextView.setGravity(Gravity.CENTER);
                tab.setCustomView(tabTextView);
                if (i == 0) {
                    tabTextView.setBackgroundResource(R.drawable.tab_selected_background);
                    tabTextView.setTextColor(Color.BLACK);
                } else {
                    tabTextView.setTextColor(Color.GRAY);
                }
            }
        }


        swipeRefreshLayout = new SwipeRefreshLayout(context);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 24;
                outRect.right = 24;
                outRect.top = 24;
            }
        });

        getCardHelper = new GetCardService(context);
        swipeRefreshLayout.addView(recyclerView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        cardAdapter = new CardAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(cardAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        loadCardsFromDatabase();

        ConstraintLayout.LayoutParams recyclerParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        recyclerParams.topToBottom = tabLayout.getId();
        swipeRefreshLayout.setLayoutParams(recyclerParams);

        linearLayout.addView(tabLayout);
        linearLayout.addView(swipeRefreshLayout);
        layout.addView(linearLayout);
        TextView addCardButton = new TextView(context);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(52)
        );
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = AndroidUtilities.dp(24);
        params.leftMargin = 24;
        params.rightMargin = 24;
        addCardButton.setLayoutParams(params);
        addCardButton.setGravity(Gravity.CENTER);
        addCardButton.setText(LocaleController.getString(R.string.add_new_card));
        addCardButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        addCardButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        addCardButton.setTypeface(AndroidUtilities.bold());
        addCardButton.setBackground(context.getDrawable(R.drawable.rounded_button));
        CardTypeBottomSheet bottomSheet = new CardTypeBottomSheet(context,this);

        addCardButton.setOnClickListener(v -> {
            addCardButton.setEnabled(false);
            bottomSheet.show();
            addCardButton.postDelayed(() -> addCardButton.setEnabled(true), 500);
        });

        layout.addView(addCardButton);

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                String tabText = tab.getText().toString();

                loadCardsFromDatabase();
            }

            @Override
            public void onTabUnselected(Tab tab) {}

            @Override
            public void onTabReselected(Tab tab) {}
        });

        return layout;
    }





    private  class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
        private List<GetCardBalanceModel.Cards> cardList;
        private final Context context;

        public CardAdapter(List<GetCardBalanceModel.Cards> cardList, Context context) {
            this.cardList = cardList;
            this.context = context;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            GetCardBalanceModel.Cards cardItem = cardList.get(position);
            holder.bind(cardItem, context);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.bottomMargin = AndroidUtilities.dp(0);
            layoutParams.topMargin = 0;

            if (position == cardList.size() - 1) {
                layoutParams.bottomMargin = AndroidUtilities.dp(72);
            }

            holder.itemView.setLayoutParams(layoutParams);
        }


        @Override
        public int getItemCount() {
            return cardList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void updateCardList(List<GetCardBalanceModel.Cards> newCardList) {
            this.cardList = newCardList;

            notifyDataSetChanged();
        }

        class CardViewHolder extends RecyclerView.ViewHolder {
            private final TextView cardBalanceText;
            private final TextView cardHolderNameText;
            private final TextView cardMask;
            private final ImageView cardType;
            private final ImageView copyIcon;
            private final ImageView banklogo;
            private final ConstraintLayout cardLayout;
            private CardView cardView ;


            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView=itemView.findViewById(R.id.home_card);
                copyIcon=itemView.findViewById(R.id.card_number_copy);
                banklogo = itemView.findViewById(R.id.card_left_logo);
                cardType = itemView.findViewById(R.id.card_type);
                cardBalanceText = itemView.findViewById(R.id.card_balance_text);
                cardHolderNameText = itemView.findViewById(R.id.card_holder_name_text);
                cardMask = itemView.findViewById(R.id.card_mask_text);
                cardLayout = itemView.findViewById(R.id.card_layout);
            }

            @SuppressLint("DefaultLocale")
            public void bind(GetCardBalanceModel.Cards cardItem,Context context) {

                Drawable drawable;
                String bankLogoUrl = BuildConfig.STATIC_URL + "app-superapp/static" + cardItem.getImageLog();
                if (bankLogoUrl != null && !bankLogoUrl.isEmpty()) {
                    Log.d("GlideDebug", "Attempting to load image: " + bankLogoUrl);
                    Glide.with(context)
                            .load(bankLogoUrl)
                            .apply(new RequestOptions().transform(new RoundedCorners(8)).format(DecodeFormat.PREFER_ARGB_8888))  // Adjust size and apply rounded corners
                            .into(banklogo);
                } else {
                    banklogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.error_in_my_id));
                }
                if (cardItem.getPcType() == 3) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.humo_card);
                    cardType.setImageDrawable(drawable);
                } else if (cardItem.getPcType() == 1) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_uzcard);
                    cardType.setImageDrawable(drawable);
                } else if (cardItem.getPcType() == 2) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_tkb_card);
                    cardType.setImageDrawable(drawable);
                }
                String cardBackgroundUrl = cardItem.getImages();
                if (cardBackgroundUrl != null && !cardBackgroundUrl.isEmpty()) {
                    Glide.with(context)
                            .load(BuildConfig.STATIC_URL +"app-superapp/static"+cardBackgroundUrl)
                            .apply(new RequestOptions().transform(new RoundedCorners(1)))
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    cardLayout.setBackground(resource);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    GradientDrawable background = new GradientDrawable(
                                            GradientDrawable.Orientation.LEFT_RIGHT,
                                            new int[]{Color.parseColor("#5A8FBB"), Color.parseColor("#5A8FBB")}
                                    );
                                    background.setCornerRadius(32f);
                                    cardLayout.setBackground(background);
                                }
                            });
                } else {
                    GradientDrawable background = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor("#5A8FBB"), Color.parseColor("#5A8FBB")}
                    );
                    background.setCornerRadius(32f);
                    cardLayout.setBackground(background);
                }
                CardView.LayoutParams params = new CardView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        AndroidUtilities.dp(172)
                );
                if(cardItem.isActive()){

                    if (cardItem.getPcType()==2){
                        copyIcon.setVisibility(GONE);
                        cardBalanceText.setText("****");
                    }else {
                        copyIcon.setVisibility(VISIBLE);
                        Drawable drawableIcon;
                        drawableIcon = ContextCompat.getDrawable(context, R.drawable.white_copy_icon);
                        copyIcon.setImageDrawable(drawableIcon);
                        copyIcon.setOnClickListener(v -> {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (clipboard != null) {
                                Toast.makeText(context,cardItem.getCardNumber(),Toast.LENGTH_LONG).show();
                                ClipData clip = ClipData.newPlainText("Copied", cardItem.getCardNumber());
                                clipboard.setPrimaryClip(clip);
                            }
                        });
                        cardBalanceText.setText( formatWithSpaces(cardItem.getBalance()));
                    }
                    cardHolderNameText.setText(cardItem.getCardOwnerName());
                    cardMask.setText(maskCardNumber(cardItem.getCardNumber()));
                    cardHolderNameText.setTextColor(Color.WHITE);
                }else {
                    copyIcon.setVisibility(GONE);
                    cardBalanceText.setText("");
                    cardHolderNameText.setText("Karta bloklangan");
                    cardHolderNameText.setTextColor(Color.RED);
                    cardMask.setText("");
                }
                cardLayout.setLayoutParams(params);
                itemView.setOnClickListener(v -> {
                    itemView.setEnabled(false);
                    if (cardItem.isActive()) {
                        cardBlockectext = "Kartani bloklash";
                        cardBlockectDialod = "bloklamoqchimisiz";
                        lockId = R.drawable.ic_block_card;
                    } else {
                        cardBlockectext = "Kartani blokdan chiqarish";
                        cardBlockectDialod = "blokdan chiqarmoqchimisiz";
                        lockId = R.drawable.ic_open_lock;
                    }
                    showBottomSheet(context, cardItem);
                    itemView.postDelayed(() -> itemView.setEnabled(true), 500);
                });

            }
        }

    }


    private void showBottomSheet(Context context, GetCardBalanceModel.Cards getCardBalanceModel) {
        bottomSheetDialog = new BottomSheetDialog(context,R.style.RoundedBottomSheetDialog);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 0, 32,0 );

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.WHITE);
        float[] cornerRadii = new float[] {
                50, 50,   // Top-left radius in pixels
                50, 50,   // Top-right radius in pixels
                0, 0,     // Bottom-right radius
                0, 0      // Bottom-left radius
        };
        background.setCornerRadii(cornerRadii);
        layout.setBackground(background);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                AndroidUtilities.dp(24),
                AndroidUtilities.dp(24)
        );

        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setWeightSum(3);
        topRow.setPadding(0, 32, 0, 32);

        LinearLayout detailsLayout = createTopItem(context, R.drawable.ic_details, "Реквизиты");
        detailsLayout.setOnClickListener(v -> {
            presentFragment(new CardDetailsFragment(getCardBalanceModel));
            bottomSheetDialog.cancel();
        });

        LinearLayout paymentLayout = createTopItem(context, R.drawable.ic_oplati, "Платежи");
        paymentLayout.setOnClickListener(v -> {
            presentFragment(new PaynetCategoryFragment());
            bottomSheetDialog.cancel();
        });

        LinearLayout transferLayout = createTopItem(context, R.drawable.ic_perevod, "Переводы");
        transferLayout.setOnClickListener(v -> {
            presentFragment(new CardInfoFragment(getCardBalanceModel.getId()));
            bottomSheetDialog.cancel();
        });

        topRow.addView(detailsLayout, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topRow.addView(paymentLayout, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topRow.addView(transferLayout, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        layout.addView(topRow);

        View divider = new View(context);
        divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout.addView(divider, dividerParams);

        layout.addView(createRow(context, R.drawable.ic_monitoring, "Мониторинг", v->{
            presentFragment(new MonitoringFragment());
            bottomSheetDialog.cancel();
        }));
        CardNameBottomSheet bottomSheet=new CardNameBottomSheet(context,this,getCardBalanceModel.getId());
        layout.addView(createRow(context, R.drawable.ic_card_settings, "Настройки карты",v->{
            bottomSheet.show();
            bottomSheetDialog.cancel();
        }));
        layout.addView(createRow(context, lockId, getCardBalanceModel.isActive()?"Заблокировать карту":"Разблокировать карту",  v -> {
            showBlockedDialog(context, getCardBalanceModel.getId(), !getCardBalanceModel.isActive());
            bottomSheetDialog.cancel();
        }));
        layout.addView(createRow(context, R.drawable.ic_delete_card, "Удалить карту",  v -> {
            showBasicDialog(context, getCardBalanceModel.getId());
            bottomSheetDialog.cancel();
        }));

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }

    private LinearLayout createRow(Context context, int iconId, String text, int textColor) {
        return createRow(context, iconId, text, null);
    }

    private LinearLayout createRow(Context context, int iconId, String text, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(32, 32, 32, 32);

        ImageView icon = new ImageView(context);
        icon.setImageResource(iconId);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(24), AndroidUtilities.dp(24));
        icon.setLayoutParams(iconParams);

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(24, 0, 0, 0);

        row.addView(icon);
        row.addView(textView);

        if (listener != null) {
            row.setOnClickListener(listener);
        }

        return row;
    }

    private LinearLayout createTopItem(Context context, int iconResId, String labelText) {
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setPadding(16, 0, 16, 0); // Adjust padding as needed

        ImageView icon = new ImageView(context);
        icon.setImageResource(iconResId);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(32), AndroidUtilities.dp(32));
        icon.setLayoutParams(iconParams);

        TextView text = new TextView(context);
        text.setText(labelText);
        text.setGravity(Gravity.CENTER);
        text.setTextSize(12);
        text.setTextColor(Color.parseColor("#000000"));

        itemLayout.addView(icon);
        itemLayout.addView(text);

        return itemLayout;
    }

    private void refreshContent() {
        new Handler().postDelayed(() -> {
            getCardBalance();
        }, 2000);
    }
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private void deleteCard(int id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_id", id);
        executorService.execute(() -> {
            cardDao.deleteCardById(id);
        });
        getCardHelper.delete(new ApiCallback<String>() {
            @Override
            public void onSuccess(String text) {
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                getCardBalance();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Xatolik yuz berdi: " + errorMessage, Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Error Message", errorMessage);
                clipboard.setPrimaryClip(clip);
            }
        }, jsonObject);
    }
    public void showBasicDialog(Context context, int id) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_card, null);

        // Initialize the views from the custom layout
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_button);

        title.setText("Удалить карту");
        message.setText("Вы действительно хотите удалить эту карту?");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_dialog_background));

            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.75);
            int height = (int) AndroidUtilities.dp(180);
            dialog.getWindow().setLayout(width, height);
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {
            deleteCard(id);
            dialog.dismiss();
        });
    }

    private void getCardBalance() {
        swipeRefreshLayout.setRefreshing(true); // Refreshni boshlash
        getCardHelper.fetchCardBalance(new ApiCallback<GetCardBalanceModel>() {
            @Override
            public void onSuccess(GetCardBalanceModel response) {
                if (response.getCards().isEmpty()) {
                    try {
                        Toast.makeText(getContext(), "Kartalar mavjud emas", Toast.LENGTH_SHORT).show();
                        finishFragment();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new Thread(() -> {
                            clearDatabase();
                            saveDataToDatabase(response);

                            // Filtrlarni darhol qo'llash
                            List<GetCardBalanceModel.Cards> filteredCards = applyFilters(response.getCards());

                            getParentActivity().runOnUiThread(() -> {
                                cardAdapter.updateCardList(filteredCards);
                                cardAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            });
                        }).start();
                    } catch (Exception e) {
                        String errorMessage = "Error: " + e.toString();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void showBlockedDialog(Context context,int id,boolean isBolcked) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_card, null);

        // Initialize the views from the custom layout
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button blocButton = dialogView.findViewById(R.id.delete_button);
       if (isBolcked){
           title.setText("Разблокировать карту");
           message.setText("Вы уверены, что хотите разблокировать эту карту?");
           blocButton.setText("Разблокировать");
       }else{
           title.setText("Заблокировать карту");
           message.setText("Вы действительно хотите заблокировать эту карту?");
           blocButton.setText("Блокировать");
       }
        dialogView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_dialog_background));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_dialog_background));
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.75);
            int height = (int) AndroidUtilities.dp(180);
            dialog.getWindow().setLayout(width, height);
        }
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        blocButton.setOnClickListener(v -> {
            cardBlocked(id,isBolcked);
            dialog.dismiss();
        });

    }
    private void cardBlocked(int id,boolean isBlocked) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_id", id);
        jsonObject.addProperty("is_blocked",isBlocked);
        getCardHelper.cardBlocked(new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                getCardBalance();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Xatolik yuz berdi: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
    }
    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ")+" UZS"; // Replace commas with spaces
    }

    public static List<GetCardBalanceModel.Cards> filterCardsByPSType(List<GetCardBalanceModel.Cards> cards) {
        List<GetCardBalanceModel.Cards> filteredCards = new ArrayList<>();
        for (GetCardBalanceModel.Cards card : cards) {
            if (card.getPcType() == 1 || card.getPcType() == 3) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }
    public static List<GetCardBalanceModel.Cards> international(List<GetCardBalanceModel.Cards> cards) {
        List<GetCardBalanceModel.Cards> filteredCards = new ArrayList<>();

        for (GetCardBalanceModel.Cards card : cards) {
            if (card.getPcType() == 2 ) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber.length() == 16) {
            return cardNumber.substring(0, 4) + " " +
                    cardNumber.substring(4, 6) + "••  •••• " +
                    cardNumber.substring(12);
        } else {
            return cardNumber;
        }
    }
    private void loadCardsFromDatabase() {
        cardDao.getAllCardBalancesWithCards().observe(getViewLifecycleOwner(), cards -> {
            if (cards != null && !cards.cards.isEmpty()) {
                response = cards;
                List<GetCardBalanceModel.Cards> filteredCards;
                if (tabLayout.getSelectedTabPosition() == 0) {
                    filteredCards = response.cards;
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    filteredCards = filterCardsByPSType(response.cards);
                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    filteredCards = international(response.cards);
                } else {
                    filteredCards = new ArrayList<>();
                }

                cardAdapter.updateCardList(filteredCards);
            } else {
                cardAdapter.updateCardList(new ArrayList<>());
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }
    private void saveDataToDatabase(GetCardBalanceModel balanceModel) {
        new Thread(() -> {
            CardsDao cardsDao = database.cardsDao();
            cardsDao.clearAllBalances();
            cardsDao.clearAllCards();
            long balanceId = cardsDao.insertBalanceModel(balanceModel);
            List<GetCardBalanceModel.Cards> cards = balanceModel.getCards();
            if (cards != null) {
                for (GetCardBalanceModel.Cards card : cards) {
                    card.setBalanceModelId((int) balanceId);
                }
                cardsDao.insertCards(cards);
            }
        }).start();
    }
    public void clearDatabase() {
        new Thread(() -> {
            database.cardsDao().clearAllBalances();
            database.cardsDao().clearAllCards();
        }).start();
    }
    private List<GetCardBalanceModel.Cards> applyFilters(List<GetCardBalanceModel.Cards> cards) {
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            return cards;
        } else if (selectedTab == 1) {
            return filterCardsByPSType(cards);
        } else if (selectedTab == 2) {
            return international(cards);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}

