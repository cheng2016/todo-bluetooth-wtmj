package com.wistron.swpc.android.WiTMJ.util.googlemap;

import java.util.List;

public interface RoutingListener {
    void onRoutingFailure(RouteException e);

    void onRoutingStart();

    void onRoutingSuccess(List<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}
