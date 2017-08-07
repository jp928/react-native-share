import React from 'react';
import {
  Animated,
  View
} from 'react-native';

const DEFAULT_ANIMATE_TIME = 300;

export default class Sheet extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      translateY: new Animated.Value(0),
      sheetHeight: parseInt(props.sheetHeight, 10) || 350
    };
  }
  componentWillReceiveProps(newProps) {
    const { translateY, sheetHeight } = this.state;
    return Animated.timing(translateY, {
      toValue: newProps.visible ? 0 : sheetHeight,
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
