package com.mirkowu.lib_util.pattern;

public abstract class InterceptChain<T> {
    private InterceptChainManager<T> mManager;

    protected void intercept(InterceptChainManager<T> manager, T data) {
        this.mManager = manager;
        intercept(data);
    }

    /**
     * 执行下一个链路
     *
     * @param data
     */
    public void next(T data) {
        if (mManager != null) {
            mManager.next(data);
        }
    }

    /**
     * 此方法内执行操作逻辑，执行完后，需要执行下一链路时，调用{@link #next(T)}
     *
     * @param data
     */
    public abstract void intercept(T data);
}
