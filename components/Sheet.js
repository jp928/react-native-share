import React from 'react';
import {
  Animated,
  StyleSheet,
  View,
  Dimensions
} from 'react-native';

const DEFAULT_BOTTOM = -300;
const DEFAULT_ANIMATE_TIME = 300;

export default class Sheet extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      translateY: new Animated.Value(DEFAULT_BOTTOM)
    };
  }
  componentWillReceiveProps(newProps) {
    return Animated.timing(this.state.translateY, {
        toValue: newProps.visible ? 0 : DEFAULT_BOTTOM,
        duration: DEFAULT_ANIMATE_TIME,
        useNativeDriver: true
    }).start();
  }

  render() {
    const { translateY } = this.state;
    return (
      <Animated.View style={{ transform: [{ translateY }]}}>
          {this.props.children}
      </Animated.View>
    );
  }
};
