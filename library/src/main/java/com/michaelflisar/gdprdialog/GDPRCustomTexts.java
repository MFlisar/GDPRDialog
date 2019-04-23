package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class GDPRCustomTexts implements Parcelable
{
    private int mTitleRes = -1;
    private String mTitle = null;
    private int mQuestionRes = -1;
    private String mQuestion = null;
    private int mMainMsgRes = -1;
    private String mMainMsg = null;
    private int mTopMsgRes = -1;
    private String mTopMsg = null;
    private int mAgeMsgRes = -1;
    private String mAgeMsg = null;

    public GDPRCustomTexts() {
        // empty default constructor
    }

    // ----------------
    // Parcelable
    // ----------------

    /**
     * sets a custom dialog main text
     *
     * @param text custom main text
     * @return this
     */
    public GDPRCustomTexts withTitle(int text)
    {
        mTitleRes = text;
        mTitle = null;
        return this;
    }

    /**
     * sets a custom dialog main text
     *
     * @param text custom main text
     * @return this
     */
    public GDPRCustomTexts withTitle(String text)
    {
        mTitleRes = -1;
        mTitle = text;
        return this;
    }

    /**
     * sets a custom dialog question
     *
     * @param question custom question
     * @return this
     */
    public GDPRCustomTexts withQuestion(int question)
    {
        mQuestionRes = question;
        mQuestion = null;
        return this;
    }

    /**
     * sets a custom dialog question
     *
     * @param question custom question
     * @return this
     */
    public GDPRCustomTexts withQuestion(String question)
    {
        mQuestionRes = -1;
        mQuestion = question;
        return this;
    }

    /**
     * sets a custom dialog main text
     *
     * @param text custom main text
     * @return this
     */
    public GDPRCustomTexts withMainText(int text)
    {
        mMainMsgRes = text;
        mMainMsg = null;
        return this;
    }

    /**
     * sets a custom dialog main text
     *
     * @param text custom main text
     * @return this
     */
    public GDPRCustomTexts withMainText(String text)
    {
        mMainMsgRes = -1;
        mMainMsg = text;
        return this;
    }

    /**
     * sets a custom dialog top text
     *
     * @param text custom top text
     * @return this
     */
    public GDPRCustomTexts withTopText(int text)
    {
        mTopMsgRes = text;
        mTopMsg = null;
        return this;
    }

    /**
     * sets a custom dialog top text
     *
     * @param text custom top text
     * @return this
     */
    public GDPRCustomTexts withTopText(String text)
    {
        mTopMsgRes = -1;
        mTopMsg = text;
        return this;
    }

    /**
     * sets a custom dialog age text
     *
     * @param text custom age text
     * @return this
     */
    public GDPRCustomTexts withAgeMsg(int text)
    {
        mAgeMsgRes = text;
        mAgeMsg = null;
        return this;
    }

    /**
     * sets a custom dialog age text
     *
     * @param text custom age text
     * @return this
     */
    public GDPRCustomTexts withAgeMsg(String text)
    {
        mAgeMsgRes = -1;
        mAgeMsg = text;
        return this;
    }

    // ----------------
    // Functions
    // ----------------

    public final boolean hasTitle()
    {
        return mTitleRes != -1 || mTitle != null;
    }

    public final String getTitle(Context context)
    {
        return mTitleRes != -1 ? context.getString(mTitleRes) : mTitle;
    }

    public final boolean hasQuestion()
    {
        return mQuestionRes != -1 || mQuestion != null;
    }

    public final String getQuestion(Context context)
    {
        return mQuestionRes != -1 ? context.getString(mQuestionRes) : mQuestion;
    }

    public final boolean hasMainText()
    {
        return mMainMsgRes != -1 || mMainMsg != null;
    }

    public final String getMainText(Context context)
    {
        return mMainMsgRes != -1 ? context.getString(mMainMsgRes) : mMainMsg;
    }

    public final boolean hasTopText()
    {
        return mTopMsgRes != -1 || mTopMsg != null;
    }

    public final String getTopText(Context context)
    {
        return mTopMsgRes != -1 ? context.getString(mTopMsgRes) : mTopMsg;
    }

    public final boolean hasAgeMsg()
    {
        return mAgeMsgRes != -1 || mAgeMsg != null;
    }

    public final String getAgeMsg(Context context)
    {
        return mAgeMsgRes != -1 ? context.getString(mAgeMsgRes) : mAgeMsg;
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRCustomTexts(Parcel in)
    {
        mTitleRes = in.readInt();
        mTitle = in.readString();
        mQuestionRes = in.readInt();
        mQuestion = in.readString();
        mMainMsgRes = in.readInt();
        mMainMsg = in.readString();
        mTopMsgRes = in.readInt();
        mTopMsg = in.readString();
        mAgeMsgRes = in.readInt();
        mAgeMsg = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mTitleRes);
        dest.writeString(mTitle);
        dest.writeInt(mQuestionRes);
        dest.writeString(mQuestion);
        dest.writeInt(mMainMsgRes);
        dest.writeString(mMainMsg);
        dest.writeInt(mTopMsgRes);
        dest.writeString(mTopMsg);
        dest.writeInt(mAgeMsgRes);
        dest.writeString(mAgeMsg);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GDPRCustomTexts createFromParcel(Parcel in)
        {
            return new GDPRCustomTexts(in);
        }

        public GDPRCustomTexts[] newArray(int size)
        {
            return new GDPRCustomTexts[size];
        }
    };
}
