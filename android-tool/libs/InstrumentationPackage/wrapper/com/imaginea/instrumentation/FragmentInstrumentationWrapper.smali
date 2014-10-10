.class public Lcom/imaginea/instrumentation/FragmentInstrumentationWrapper;
.super Ljava/lang/Object;
.source "FragmentInstrumentationWrapper.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static enableDebugLogging()V
    .registers 3

    .prologue
    const/4 v2, 0x1

    .line 6
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0xb

    if-lt v0, v1, :cond_b

    .line 7
    invoke-static {v2}, Landroid/app/FragmentManager;->enableDebugLogging(Z)V

    .line 10
    :goto_a
    return-void

    .line 9
    :cond_b
    invoke-static {v2}, Landroid/support/v4/app/FragmentManager;->enableDebugLogging(Z)V

    goto :goto_a
.end method
