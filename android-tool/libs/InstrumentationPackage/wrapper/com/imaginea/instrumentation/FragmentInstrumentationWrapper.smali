.class public Lcom/imaginea/instrumentation/FragmentInstrumentationWrapper;
.super Ljava/lang/Object;
.source "FragmentInstrumentationWrapper.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "x-mobile"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 5
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static enableDebugLogging()V
    .registers 1

    .prologue
    const/4 v0, 0x1

    .line 11
    invoke-static {v0}, Landroid/app/FragmentManager;->enableDebugLogging(Z)V

    .line 12
    invoke-static {v0}, Landroid/support/v4/app/FragmentManager;->enableDebugLogging(Z)V

    .line 13
    return-void
.end method
